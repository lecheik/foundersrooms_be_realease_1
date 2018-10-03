package com.foundersrooms.service;

import com.foundersrooms.domain.project.step.ProjectStepAttachment;
import com.foundersrooms.domain.project.step.ProjectStepDeliverable;
import com.foundersrooms.domain.project.step.ProjectStepNote;
import com.foundersrooms.domain.project.step.ProjectStepRemark;

public interface ProjectWorkflowStepService {

	boolean isProjectStepAlreadyPersisted(Long projectId, int stepNum);
	
	ProjectStepNote createProjectStepNote(ProjectStepNote psn,Long projectId, int stepNum);
	
	void removeStepNote(Long noteId);
	
	ProjectStepNote updateProjectStepNote(ProjectStepNote psn, Long psnId);
	
	ProjectStepRemark createProjectStepRemark(ProjectStepRemark psr,Long projectId, int stepNum);
	
	ProjectStepAttachment createProjectStepAttachment(ProjectStepAttachment psa, Long projectId, int stepNum);
	
	String removeStepAttachement(Long attachmentId);
	
	ProjectStepDeliverable initializeProjectStepDeliverableProgress(ProjectStepDeliverable psd, Long projectId, int stepNum);
	
	ProjectStepDeliverable updateProjectStepDeliverableProgress(ProjectStepDeliverable psd);
}
