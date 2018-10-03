package com.foundersrooms.controller;

import static com.foundersrooms.service.impl.UtilityServiceImpl.toSlug;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.domain.project.step.ProjectStepAttachment;
import com.foundersrooms.domain.project.step.ProjectStepDeliverable;
import com.foundersrooms.domain.project.step.ProjectStepNote;
import com.foundersrooms.domain.project.step.ProjectStepRemark;
import com.foundersrooms.service.AmazonClient;
import com.foundersrooms.service.ProjectService;
import com.foundersrooms.service.ProjectStepService;
import com.foundersrooms.service.ProjectWorkflowStepService;
import com.foundersrooms.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@RestController
@RequestMapping("/project")
public class ProjectResource {

	@Autowired	
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectStepService projectStepService;
	
	@Autowired
	private ProjectWorkflowStepService projectWorkflowStepService;
	
	@Autowired
	private AmazonClient amazonS3Client;	
	
	Gson gson=new Gson();
	
	@RequestMapping(value="/")
	public Object getAllMyProjects(Principal principal){
		User user=userService.findByUsername(principal.getName());	
		//System.out.println("requester "+user.getId()+" "+ user.getUsername());
		return projectService.getMyProjects(user.getId());
	}
	
	@RequestMapping(value="/get_project/{project_id}")
	public ResponseEntity getProjectById(@PathVariable("project_id") Long projectId){		
		return new ResponseEntity(projectService.getProjectById(projectId), HttpStatus.OK);
	}	
	
	@RequestMapping(value="/get_project_by_name/{project_name}")
	public ResponseEntity getProjectByName(@PathVariable("project_name") String projectName){		
		return new ResponseEntity(projectService.findProjectByNameSlug(projectName), HttpStatus.OK);
	}		
	
	@RequestMapping(value="/add_member_to_project/{project_id}", method = RequestMethod.POST)
	public ResponseEntity addProjectMember(@PathVariable("project_id") Long projectId,@RequestBody String userId) {				
		return new ResponseEntity(projectService.addProjectMember(projectId, gson.fromJson(userId, Long.class)), HttpStatus.OK);
	}
	
	@RequestMapping(value="/step/create_task/{project_id}",method=RequestMethod.POST)
	public ResponseEntity createStepTask(@RequestBody String parameter,@PathVariable("project_id") Long projectId) {
		return new ResponseEntity(projectStepService.createStepTask(projectId,parameter),HttpStatus.OK);
	}
	
	@RequestMapping(value="/step/update_task/{project_id}",method=RequestMethod.POST)
	public ResponseEntity updateStepTask(@PathVariable("project_id") Long projectId,@RequestBody String taskRawData) {
		return new ResponseEntity(projectStepService.updateStepTask(projectId, taskRawData),HttpStatus.OK);
	}
	
	@RequestMapping(value="/step/assign_task/{task_id}",method=RequestMethod.POST)
	public ResponseEntity assignTaskToProjecMember(@RequestBody String memberId,@PathVariable("task_id") Long taskId) {
		Long projectMemberId=gson.fromJson(memberId,Long.class);
		return new ResponseEntity(projectStepService.assignTaskToProjectMember(taskId, projectMemberId),HttpStatus.OK);
	}
	
	@RequestMapping(value="/step/delete_task/{task_id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteStepTask(@PathVariable("task_id") Long taskId) {
		try {
			projectStepService.removeStepTask(taskId);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/project_member/remove/{project_member_id}",method=RequestMethod.DELETE)
	public ResponseEntity removeProjectMember(@PathVariable("project_member_id") Long projectMemberId) {
		User removedUser;
		try {
			removedUser=projectStepService.removeProjectMember(projectMemberId);
		}catch(Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(removedUser,HttpStatus.OK);
	}
	
	@RequestMapping(value="/members/{project_id}")
	public Set<ProjectMember> getProjectMembersFromProjectId(@PathVariable("project_id") Long projectId){		
		return projectService.getProjectMembersFromProjectId(projectId);
	}
	
	@RequestMapping(value="/create_project", method = RequestMethod.POST)
	public ResponseEntity createProject(@RequestBody String postContent){
		Set<ProjectMember> projectMembers=new HashSet<>();
		Project project=gson.fromJson(postContent, Project.class);
		ProjectMember pm=new ProjectMember();
		User creator=userService.findByUsername(project.getCreator().getUsername());
		pm.setMember(creator);
		projectMembers.add(pm);
		//project.setTeamSize(1);
		try {
			project=projectService.createProject(project, projectMembers);
			return new ResponseEntity(project, HttpStatus.OK);
		}catch(Exception ex) {		
			return new ResponseEntity("Project Already Exists", HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/step/create/{project_id}",method=RequestMethod.POST)
	public ResponseEntity createProjectStep(@PathVariable("project_id") Long projectId, @RequestBody String stepRawData) {
		return new ResponseEntity(projectStepService.saveProjectStep(projectId, stepRawData),HttpStatus.OK);
	}
	
	@RequestMapping(value="/update_project_details/{project_id}", method = RequestMethod.POST)
	public ResponseEntity updateProjectDetails(@PathVariable("project_id") Long projectId, @RequestBody String postContent){
		Project databaseItem=projectService.getProjectById(projectId);
		Project requestItem=gson.fromJson(postContent, Project.class);
		//databaseItem.setName(requestItem.getName());
		databaseItem.setDescription(requestItem.getDescription());
		databaseItem.setVideoPitch(requestItem.getVideoPitch());		
		databaseItem=projectService.saveProject(databaseItem);
		//align elastic search item
		Project elasticProject=projectService.findElasticProjectById(projectId);
		if(elasticProject!=null) {
			elasticProject.setDescription(requestItem.getDescription());
			elasticProject.setVideoPitch(requestItem.getVideoPitch());
			projectService.saveElasticProject(elasticProject);
		}
		return new ResponseEntity(databaseItem, HttpStatus.OK);
	}	
	
	@RequestMapping(value="/update_project_state/{project_id}", method = RequestMethod.POST)
	public ResponseEntity updateProjectSate(@PathVariable("project_id") Long projectId,@RequestBody String projectState){		
		Project result=projectService.updateProjectState(projectId, gson.fromJson(projectState, boolean.class));
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/save_updated_project", method = RequestMethod.POST)
	public ResponseEntity saveUpdatedProject(@RequestBody String parameter) {
		Project project=gson.fromJson(parameter, Project.class);
		project=projectService.saveUpdatedProject(project);
		return new ResponseEntity(project,HttpStatus.OK);
	}
		
	
	@RequestMapping(value="delete_project/{project_id}", method = RequestMethod.DELETE)
	public ResponseEntity deleteProject(@PathVariable("project_id") Long projectId){		
		projectService.deleteProject(projectId);
		return new ResponseEntity("Project Successfully deleted", HttpStatus.OK);
	}
	
	//project notes management
	class JsonDateDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
		      String s = json.getAsJsonPrimitive().getAsString();
		      long l = Long.parseLong(s.substring(6, s.length() - 2));
		      Date d = new Date(l);
		      return d; 
		} 
		}
	
	
	//notes management
	@RequestMapping(value="create_note/{project_id}/{step_num}",method=RequestMethod.POST)
	public ResponseEntity addANote(@PathVariable("project_id") Long projectId,@PathVariable("step_num") int stepNum,@RequestBody String parameter) {
		gson=new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
		ProjectStepNote stepNote=gson.fromJson(parameter, ProjectStepNote.class);
		//projectWorkflowStepService.createProjectStepNote(stepNote,projectId,stepNum);
		return new ResponseEntity(projectWorkflowStepService.createProjectStepNote(stepNote,projectId,stepNum),HttpStatus.OK);
	}
	
	@RequestMapping(value="remove_note/{note_id}", method=RequestMethod.DELETE)
	public ResponseEntity removeNote(@PathVariable("note_id") Long noteId) {
		projectWorkflowStepService.removeStepNote(noteId);
		return new ResponseEntity("Item successfully removed",HttpStatus.OK);
	}
	
	@RequestMapping(value="update_note/{note_id}", method=RequestMethod.POST)
	public ResponseEntity updateNote(@PathVariable("note_id") Long noteId,@RequestBody String parameter) {
		gson=new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
		ProjectStepNote stepNote=gson.fromJson(parameter, ProjectStepNote.class);	
		stepNote=projectWorkflowStepService.updateProjectStepNote(stepNote,noteId);
		return new ResponseEntity(stepNote,HttpStatus.OK);
	}
	
	//comments management
	@RequestMapping(value="create_comment/{project_id}/{step_num}",method=RequestMethod.POST)
	public ResponseEntity addAComment(@PathVariable("project_id") Long projectId,@PathVariable("step_num") int stepNum,@RequestBody String parameter) {
		gson=new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
		ProjectStepRemark stepComment=gson.fromJson(parameter, ProjectStepRemark.class);		
		ProjectStepRemark result=projectWorkflowStepService.createProjectStepRemark(stepComment,projectId,stepNum);
		return new ResponseEntity(result,HttpStatus.OK);
	}	
	
	@RequestMapping(value="create_attachment/{project_id}/{step_num}",method=RequestMethod.POST)
	public ResponseEntity addAnAttachment(Principal principal, @RequestPart("ad") String adString, @RequestPart("file") MultipartFile file,@PathVariable("project_id") Long projectId,@PathVariable("step_num") int stepNum) throws com.fasterxml.jackson.core.JsonParseException, JsonMappingException, IOException {		
		gson=new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
		ProjectStepAttachment attachment = gson.fromJson(adString, ProjectStepAttachment.class);
		User user=userService.findByUsername(principal.getName());
		attachment.setUserReference(user);
		attachment=projectWorkflowStepService.createProjectStepAttachment(attachment, projectId, stepNum);
		User author=attachment.getWorflowStepReference().getProjectReference().getCreator();
		Project project=attachment.getWorflowStepReference().getProjectReference();
		String prefix=toSlug(author.getUsername())+"/"+toSlug(project.getName())+"/"+attachment.getWorflowStepReference().getStepNum()+"/"+attachment.getId()+"/"+attachment.getAttachmentName() ;
		amazonS3Client.createFileInsideBucket(prefix, file);
		return new ResponseEntity(attachment,HttpStatus.OK);
	}
	
	@RequestMapping(value="delete_attachement/{attachment_id}", method=RequestMethod.DELETE)
	public ResponseEntity deleteAnAttachment(@PathVariable("attachment_id") Long attachmentId) {
		String s3Prefix=projectWorkflowStepService.removeStepAttachement(attachmentId);
		amazonS3Client.deleteObject(s3Prefix);
		return new ResponseEntity("Item successfully removed",HttpStatus.OK);
	}
	
	private class PMAdapter{
		public List<Long> membersId;
	}
	
	@RequestMapping(value="add_project_members/{project_id}",method=RequestMethod.POST)
	public ResponseEntity addProjectMembers(@PathVariable("project_id") Long projectId, @RequestBody String parameter) {
		PMAdapter adapter=gson.fromJson(parameter, PMAdapter.class);
		Set<ProjectMember> result=projectService.addProjectMembers(projectId, adapter.membersId);
		return new ResponseEntity(result,HttpStatus.OK);
	}
	
	@RequestMapping(value="get_active_members/{project_id}",method=RequestMethod.GET)
	public ResponseEntity getActiveProjectMembers(@PathVariable("project_id") Long projectId) {
		Set<ProjectMember> result=projectService.getActiveProjectMembers(projectId);
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="initiate_step_deliverable_progress/{project_id}/{step_num}",method=RequestMethod.POST)
	public ResponseEntity initiateDeliverableProgress(@PathVariable("project_id") Long projectId,@PathVariable("step_num") int stepNum, @RequestBody String parameter) {
		ProjectStepDeliverable deliverable = gson.fromJson(parameter, ProjectStepDeliverable.class);
		deliverable = projectWorkflowStepService.initializeProjectStepDeliverableProgress(deliverable, projectId, stepNum);
		return new ResponseEntity(deliverable, HttpStatus.OK);
	}
	
	@RequestMapping(value="update_step_deliverable_progress/{project_id}/{step_num}",method=RequestMethod.POST)
	public ResponseEntity updateDeliverableProgress(@PathVariable("project_id") Long projectId,@PathVariable("step_num") int stepNum, @RequestBody String parameter) {
		ProjectStepDeliverable deliverable = gson.fromJson(parameter, ProjectStepDeliverable.class);
		deliverable = projectWorkflowStepService.updateProjectStepDeliverableProgress(deliverable);
		return new ResponseEntity(deliverable, HttpStatus.OK);
	}	
}
