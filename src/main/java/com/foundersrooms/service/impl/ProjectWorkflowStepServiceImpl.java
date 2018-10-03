package com.foundersrooms.service.impl;


import static com.foundersrooms.service.impl.UtilityServiceImpl.toSlug;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.domain.project.step.ProjectStepAttachment;
import com.foundersrooms.domain.project.step.ProjectStepDeliverable;
import com.foundersrooms.domain.project.step.ProjectStepNote;
import com.foundersrooms.domain.project.step.ProjectStepRemark;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken.OperationType;
import com.foundersrooms.domain.project.step.ProjectWebSocketToken.StepContentType;
import com.foundersrooms.domain.project.step.ProjectWorkflowStep;
import com.foundersrooms.repository.ProjectMemberRepository;
import com.foundersrooms.repository.ProjectRepository;
import com.foundersrooms.repository.ProjectStepAttachmentRepository;
import com.foundersrooms.repository.ProjectStepDeliverableRepository;
import com.foundersrooms.repository.ProjectStepNoteRepository;
import com.foundersrooms.repository.ProjectStepRemarkRepository;
import com.foundersrooms.repository.ProjectWorkflowStepRepository;
import com.foundersrooms.service.ProjectWorkflowStepService;

@Service
public class ProjectWorkflowStepServiceImpl implements ProjectWorkflowStepService {

	@Autowired
	private ProjectWorkflowStepRepository projectWorkflowStepRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectStepNoteRepository projectStepNoteRepository;
	
	@Autowired
	private ProjectStepRemarkRepository projectStepRemarkRepository;
	
	@Autowired
	private ProjectStepAttachmentRepository projectStepAttachmentRepository;
	
	@Autowired
	private ProjectStepDeliverableRepository projectStepDeliverableRepository;
	
	@Autowired
	private ProjectMemberRepository projectMemberRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public boolean isProjectStepAlreadyPersisted(Long projectId, int stepNum) {
		// TODO Auto-generated method stub
		Set<ProjectWorkflowStep> result=projectWorkflowStepRepository.getWorkflowStepByStepNum(stepNum, projectId);
		if(result!=null)
			if(result.size()!=0)
				return true;
		return false;
	}
	
	private ProjectWorkflowStep createStepDeliverableTemplate(ProjectWorkflowStep pwStep,List<String> deliverables) {
		for(int i=0;i<deliverables.size();i++) {
			ProjectStepDeliverable stepDeliverable=new ProjectStepDeliverable();
			stepDeliverable.setCompleted(false);
			stepDeliverable.setDeliverableNum(i);
			stepDeliverable.setLabel(deliverables.get(i));
			stepDeliverable.setProgress(0);
			stepDeliverable.setLabelSlug(toSlug(deliverables.get(i)));
			stepDeliverable.setWorflowStepReference(pwStep);
			stepDeliverable=projectStepDeliverableRepository.save(stepDeliverable);
			pwStep.getDeliverablesModelStatus().add(stepDeliverable);
			pwStep=projectWorkflowStepRepository.save(pwStep);
		}		
		return pwStep;
	}

	@Override
	public ProjectStepNote createProjectStepNote(ProjectStepNote psn, Long projectId, int stepNum) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findOne(projectId);
		ProjectWorkflowStep pwStep;
		if(isProjectStepAlreadyPersisted(projectId,stepNum)) {
			pwStep=(ProjectWorkflowStep)projectWorkflowStepRepository.getWorkflowStepByStepNum(stepNum, projectId).toArray()[0];
		}else {
			pwStep=psn.getWorflowStepReference();
			List<String> deliverables=pwStep.getDeliverables();
			pwStep.setProjectReference(project);
			pwStep=projectWorkflowStepRepository.save(pwStep);
			pwStep=createStepDeliverableTemplate(pwStep,deliverables);						
			project.getProjectWfSteps().add(pwStep);
			project=projectRepository.save(project);			
		}
		psn.setCreationDate(new Date());
		psn.setLastModidicationDate(new Date());
		psn.setWorflowStepReference(pwStep);
		psn=projectStepNoteRepository.save(psn);
		pwStep.getNotes().add(psn);
		pwStep=projectWorkflowStepRepository.save(pwStep);	
		//websocket message construction
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.NOTE);
		token.setOperation(OperationType.ADD);
		token.setProjectStepNote(psn);
		token.setAuthorId(project.getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);
		return psn;
	}

	@Override
	@Transactional
	public void removeStepNote(Long noteId) {
		// TODO Auto-generated method stub		
		projectStepNoteRepository.deleteProjectStepNote(noteId);
	}

	@Override
	public ProjectStepNote updateProjectStepNote(ProjectStepNote psn, Long psnId) {
		// TODO Auto-generated method stub
		ProjectStepNote dbInstance=projectStepNoteRepository.findOne(psnId);
		dbInstance.setContent(psn.getContent());
		dbInstance.setLastModidicationDate(new Date());
		psn=projectStepNoteRepository.save(dbInstance);
		return psn;
	}

	@Override
	public ProjectStepRemark createProjectStepRemark(ProjectStepRemark psr, Long projectId, int stepNum) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findOne(projectId);
		ProjectWorkflowStep pwStep;
		if(isProjectStepAlreadyPersisted(projectId,stepNum)) {
			pwStep=(ProjectWorkflowStep)projectWorkflowStepRepository.getWorkflowStepByStepNum(stepNum, projectId).toArray()[0];
		}else {
			pwStep=psr.getWorflowStepReference();
			List<String> deliverables=pwStep.getDeliverables();
			pwStep.setProjectReference(project);
			pwStep=projectWorkflowStepRepository.save(pwStep);			
			pwStep=createStepDeliverableTemplate(pwStep,deliverables);					
			project.getProjectWfSteps().add(pwStep);
			project=projectRepository.save(project);
			
		}
		User userReference=psr.getUserReference();
		ProjectMember pm=(ProjectMember)projectMemberRepository.findByUserNameAndProjectName(userReference.getUsername(), project.getName()).toArray()[0];
		psr.setAuthor(pm);
		psr.setCreationDate(new Date());
		psr.setLastModidicationDate(new Date());
		psr.setWorflowStepReference(pwStep);
		psr=projectStepRemarkRepository.save(psr);
		pm.getProjectMemberRemarks().add(psr);
		pm=projectMemberRepository.save(pm);
		pwStep.getRemarks().add(psr);
		pwStep=projectWorkflowStepRepository.save(pwStep);	
		//websocket message construction
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.COMMENT);
		token.setOperation(OperationType.ADD);
		token.setProjectStepRemark(psr);
		token.setAuthorId(psr.getAuthor().getMember().getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);		
		return psr;
	}
	
	private String generateS3Prefix(ProjectWorkflowStep pwStep,ProjectStepAttachment psa) {
		Project project=pwStep.getProjectReference();
		User projectAuthor=project.getCreator();
		return toSlug(projectAuthor.getUsername())+"/"+toSlug(project.getName())+"/"+psa.getWorflowStepReference().getStepNum()+"/"+psa.getId()+"/"+psa.getAttachmentName() ;				
	}

	@Override
	public ProjectStepAttachment createProjectStepAttachment(ProjectStepAttachment psa, Long projectId, int stepNum) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findOne(projectId);
		ProjectWorkflowStep pwStep;
		if(isProjectStepAlreadyPersisted(projectId,stepNum)) {
			pwStep=(ProjectWorkflowStep)projectWorkflowStepRepository.getWorkflowStepByStepNum(stepNum, projectId).toArray()[0];
		}else {
			pwStep=psa.getWorflowStepReference();
			List<String> deliverables=pwStep.getDeliverables();
			pwStep.setProjectReference(project);
			pwStep=projectWorkflowStepRepository.save(pwStep);
			pwStep=createStepDeliverableTemplate(pwStep,deliverables);						
			project.getProjectWfSteps().add(pwStep);
			project=projectRepository.save(project);
			
		}
		User userReference=psa.getUserReference();		
		ProjectMember pm=(ProjectMember)projectMemberRepository.findByUserNameAndProjectName(userReference.getUsername(), project.getName()).toArray()[0];
		psa.setAuthor(pm);
		psa.setUploadDate(new Date());		
		psa.setWorflowStepReference(pwStep);
		psa=projectStepAttachmentRepository.save(psa);
		psa.setS3Prefix(generateS3Prefix(pwStep,psa));
		pm.getProjectMemberAttachment().add(psa);
		pm=projectMemberRepository.save(pm);
		pwStep.getAttachmentsMetaData().add(psa);
		pwStep=projectWorkflowStepRepository.save(pwStep);	
		//websocket message construction
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.ATTACHMENT);
		token.setOperation(OperationType.ADD);
		token.setProjectStepAttachment(psa);;
		token.setAuthorId(psa.getAuthor().getMember().getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);
		return psa;				
	}

	@Override
	@Transactional
	public String removeStepAttachement(Long attachmentId) {
		// TODO Auto-generated method stub
		ProjectStepAttachment attachment=projectStepAttachmentRepository.findOne(attachmentId);
		Project project=attachment.getWorflowStepReference().getProjectReference();
		User author=project.getCreator();		
		String prefix=toSlug(author.getUsername())+"/"+toSlug(project.getName())+"/"+attachment.getWorflowStepReference().getStepNum()+"/"+attachment.getId()+"/"+attachment.getAttachmentName() ;
		//websocket message construction
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.ATTACHMENT);
		token.setOperation(OperationType.REMOVE);
		ProjectStepAttachment psa=projectStepAttachmentRepository.findOne(attachmentId);
		ProjectWorkflowStep pwStep=psa.getWorflowStepReference();
		token.setProjectStepAttachment(psa);
		token.setAuthorId(psa.getAuthor().getMember().getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);		
		projectStepAttachmentRepository.deleteProjectStepAttachment(attachmentId);
		return prefix;
	}
	
	public void notify(ProjectWebSocketToken token, String destination) {
		messagingTemplate.convertAndSend("/topic/"+destination, token);
		return;
	}
	
	private ProjectWorkflowStep updateProjectStepRatio(ProjectWorkflowStep pws) {
		float ratio=0f;		
		for(int i=0;i<pws.getDeliverablesModelStatus().size();i++) {
			ProjectStepDeliverable psd=(ProjectStepDeliverable)pws.getDeliverablesModelStatus().toArray()[i];			
			ratio=ratio+psd.getProgress();
		}
		pws.setStepRatio(ratio);
		return projectWorkflowStepRepository.save(pws);
	}

	@Override
	@Transactional
	public ProjectStepDeliverable initializeProjectStepDeliverableProgress(ProjectStepDeliverable psd, Long projectId,
			int stepNum) {
		Project project=projectRepository.findOne(projectId);		
		ProjectWorkflowStep pwStep;
		if(isProjectStepAlreadyPersisted(projectId,stepNum)) {
			pwStep=(ProjectWorkflowStep)projectWorkflowStepRepository.getWorkflowStepByStepNum(stepNum, projectId).toArray()[0];
		}else {
			pwStep=psd.getWorflowStepReference();
			List<String> deliverables=pwStep.getDeliverables();
			pwStep.setProjectReference(project);
			pwStep=projectWorkflowStepRepository.save(pwStep);
			pwStep=createStepDeliverableTemplate(pwStep,deliverables);						
			project.getProjectWfSteps().add(pwStep);
			project=projectRepository.save(project);			
		}
		ProjectStepDeliverable dbItem=projectStepDeliverableRepository.getPSDByProjectStepAndDeliverableRef(pwStep.getId(), psd.getDeliverableNum());
		dbItem.setProgress(psd.getProgress());
		dbItem=projectStepDeliverableRepository.save(dbItem);			
		//entityManager.flush();
		//pwStep=projectWorkflowStepRepository.save(pwStep);
		updateProjectStepRatio(pwStep);
		//notification to all project stakeholders
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.DELIVERABLE);
		token.setOperation(OperationType.UPDATE);
		token.setProjectStepDeliverable(dbItem);
		token.setAuthorId(project.getCreator().getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);		
		return dbItem;
	}

	@Override
	public ProjectStepDeliverable updateProjectStepDeliverableProgress(ProjectStepDeliverable psd) {
		// TODO Auto-generated method stub
		ProjectStepDeliverable dbItem=projectStepDeliverableRepository.findOne(psd.getId());
		dbItem.setProgress(psd.getProgress());		
		dbItem=projectStepDeliverableRepository.save(dbItem);
		updateProjectStepRatio(projectWorkflowStepRepository.findOne(dbItem.getWorflowStepReference().getId()));
		ProjectWorkflowStep pwStep=dbItem.getWorflowStepReference();
		Project project=dbItem.getWorflowStepReference().getProjectReference();
		String destination=project.getCreator().getUsername()+"_"+project.getName()+"_"+project.getId();
		ProjectWebSocketToken token=new ProjectWebSocketToken();
		token.setOrder(StepContentType.DELIVERABLE);
		token.setOperation(OperationType.UPDATE);
		token.setProjectStepDeliverable(dbItem);
		token.setAuthorId(project.getCreator().getId());
		token.setGroupId(pwStep.getGroupId());
		token.setGroupStepId(pwStep.getGroupStepId());
		notify(token, destination);		
		return dbItem;
	}	

}
