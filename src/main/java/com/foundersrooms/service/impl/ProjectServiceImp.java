package com.foundersrooms.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.people.Creator;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.domain.project.Step;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken.OperationType;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken.StepContentType;
import com.foundersrooms.elasticsearchreprository.ProjectElasticRepository;
import com.foundersrooms.repository.ProjectMemberRepository;
import com.foundersrooms.repository.ProjectRepository;
import com.foundersrooms.repository.StepRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.ProjectService;

@Service
public class ProjectServiceImp implements ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectElasticRepository projectElacticRepository;
	
	@Autowired
	private ProjectMemberRepository projectMemberRepository;
	
	@Autowired
	private StepRepository stepRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;	
	
	private class ProjectCategoryWrapper{
		
		public Set<Project> active=new HashSet<>();
		public Set<Project> archived=new HashSet<>();
	}
	
	
	@Override
	public Object getMyProjects(Long myId) {
		ProjectCategoryWrapper pg=new ProjectCategoryWrapper();
		pg.active=projectRepository.getMyActiveProjects(myId);
		System.out.println((new ArrayList<Project>(pg.active)).get(0).getNameSlug());
		pg.archived=projectRepository.getMyArchiveProjects(myId);	
		return pg;
	}

	@Override
	public Project saveProject(Project project) {
		// TODO Auto-generated method stub
	    //Project result=projectRepository.findByName(project.getName());
		Project result=projectRepository.findOne(project.getId());
		if(result!=null) {
			project=projectRepository.save(project);
			return project;
		}
		return result;
		
	}

	private ProjectMember cloneProjectMember(ProjectMember pm) {
		ProjectMember result=new ProjectMember();
		result.setId(pm.getId());		
		result.setMember(pm.getMember());
		result.setProject(pm.getProject());
		result.setScope(pm.getScope());
		return result;
	}

	@Override
	public Set<ProjectMember> getProjectMembersFromProjectId(Long projectId) {
		// TODO Auto-generated method stub
		
		Set<ProjectMember> result= projectRepository.getProjectMembersFromProjectId(projectId);
		Set<ProjectMember> tempArray=new HashSet<>();
		for(ProjectMember item:result) 
			tempArray.add(cloneProjectMember(item))	;
		return result;
	}

	@Override
	public Project createProject(Project project, Set<ProjectMember> projectMembers) throws Exception {
		Project localProject = projectRepository.findByNameAllIgnoreCase(project.getName());

		if (localProject != null) {
			LOG.info("Project with name {} already exist. Nothing will be done. ", localProject.getName());
			//throw new Exception("Project Already Exists");
		} else {
			project=projectRepository.save(project);			
			for (ProjectMember pm : projectMembers) {				
				pm.setProject(project);
				project.getMembers().add(projectMemberRepository.save(pm));
				localProject=projectRepository.save(project);
			}			
		}
		projectElacticRepository.save(localProject);
		return localProject;
	}

	@Override
	public Project updateProjectState(Long projectId,boolean projectState) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findOne(projectId);
		Project elasticProject=projectElacticRepository.findOne(projectId);		
		project.setArchived(projectState);
		if(elasticProject!=null) {
		elasticProject.setArchived(projectState);
		projectElacticRepository.save(elasticProject);
		}
		return projectRepository.save(project);
		
	}

	@Override
	public void deleteProject(Long id) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findOne(id);
		projectMemberRepository.delete(project.getMembers());
		projectRepository.delete(project);
		projectElacticRepository.delete(projectElacticRepository.findOne(id));
	}

	@Override
	public Project getProjectById(Long projectId) {
		// TODO Auto-generated method stub
		Project mysqlProject=projectRepository.findOne(projectId);
		if(projectElacticRepository.findOne(projectId)==null)
			projectElacticRepository.save(mysqlProject);
		return mysqlProject;
	}
	
	@Override
	public Project findProjectByNameSlug(String param){
		Project mysqlProject=projectRepository.findByNameSlug(param);
		if(projectElacticRepository.findOne(mysqlProject.getId())==null)
			projectElacticRepository.save(mysqlProject);
		return mysqlProject;		
	}

	private Step updateProjectStep(Long stepId,Step requestItem) {
		Step databaseItem=stepRepository.findOne(stepId);
		databaseItem.setCompleted(requestItem.isCompleted());
		databaseItem.setDescription(requestItem.getDescription());
		return databaseItem;
	}
	
	@Override
	public Project saveUpdatedProject(Project requestItem) {
		// TODO Auto-generated method stub
		Project databaseItem=projectRepository.findOne(requestItem.getId());
		Set<Step> projectSteps=requestItem.getProjectSteps();
		for (Step step : projectSteps) {	
			if(step.getId()!=null) 				
				step=updateProjectStep(step.getId(),step);							
			step.setProjectReference(databaseItem);				
			databaseItem.getProjectSteps().add(stepRepository.save(step));
		}
		
		Set<ProjectMember> projectMembers=databaseItem.getMembers();
		for (ProjectMember pm : projectMembers) {	
			User user=userRepository.findOne(pm.getMember().getId());
			pm.setMember(user);	
			pm.setProject(databaseItem);
			databaseItem.getMembers().add(projectMemberRepository.save(pm));			
		}		
		databaseItem=projectRepository.save(databaseItem);
		return databaseItem;
	}

	@Override
	public ProjectMember addProjectMember(Long projectId, Long userId) {
		Project project=projectRepository.findOne(projectId);
		User user=userRepository.findOne(userId);
		ProjectMember projectMember=new ProjectMember();
		projectMember.setMember(user);
		projectMember.setProject(project);
		projectMemberRepository.save(projectMember);
		project.getMembers().add(projectMember);
		projectRepository.save(project);

		return projectMember;
	}
		
	private void alignTeamSizeValueWithElasticSearchItem(Project mainItem) {
		//align team size value with elasticsearch item
		Project elasticProject=projectElacticRepository.findOne(mainItem.getId());
		//elasticProject.setTeamSize(mainItem.getTeamSize());
		elasticProject.setTeamSize(elasticProject.getMembers().size());
		projectElacticRepository.save(elasticProject);
	}
	
	@Override
	@Transactional
	public Set<ProjectMember> addProjectMembers(Long projectId, List<Long> userIds) {
		Project project=projectRepository.findOne(projectId);
		Set<ProjectMember> result=new HashSet<>();
		projectMemberRepository.deactivateAllProjectMembers(projectId);
		for(int i=0;i<userIds.size();i++) {
			ProjectMember pm=projectMemberRepository.getProjectMemberFromUserId(projectId,userIds.get(i));
			if(pm==null) {
				User user=userRepository.findOne(userIds.get(i));
				pm=new ProjectMember();
				pm.setProject(project);
				pm.setMember(user);
				pm.setActiveMember(true);
				pm=projectMemberRepository.save(pm);
				project.getMembers().add(pm);
				project=projectRepository.save(project);				
			}else {
				pm.setActiveMember(true);
				pm=projectMemberRepository.save(pm);
			}
			result.add(pm);
		}
		
		alignTeamSizeValueWithElasticSearchItem(project);
		//notify project members
		
		Set<ProjectMember> members=project.getMembers();
		for(ProjectMember pm:members) {
			String destination="dashboard/"+pm.getMember().getUsername();
			ProjectWebSocketToken token=new ProjectWebSocketToken();
			
			token.setOrder(StepContentType.PROJECT);
			if(pm.isActiveMember())
				token.setOperation(OperationType.ADD);
			else
				token.setOperation(OperationType.REMOVE);
			token.setProject(project);
			notify(token,destination);
		}
		return result;
	}

	@Override
	public Set<ProjectMember> getActiveProjectMembers(Long projectId) {
		// TODO Auto-generated method stub		
		return projectMemberRepository.findActiveProjectMemberByProject(projectId);
	}
	
	
	public void notify(ProjectWebSocketToken token, String destination) {
		messagingTemplate.convertAndSend("/topic/"+destination, token);
		return;
	}

	@Override
	public Project findElasticProjectById(Long projectId) {
		// TODO Auto-generated method stub
		return projectElacticRepository.findOne(projectId);
	}

	@Override
	public Project saveElasticProject(Project project) {
		// TODO Auto-generated method stub
		return projectElacticRepository.save(project);
	}

	@Override
	public void updateProjectCreatorDescription(Creator user) {
		// TODO Auto-generated method stub
		Set<Project> projects=projectElacticRepository.findByCreator_Id(user.getId());
		if(projects.size()>0) {
			for(Project item:projects) {
				item.setCreator(user);		
				item=projectElacticRepository.save(item);
			}			
		}
		
	}	

}
