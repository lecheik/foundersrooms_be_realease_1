package com.foundersrooms.domain.project.step;

import com.foundersrooms.domain.project.Project;

public class ProjectWebSocketToken {
	public static enum StepContentType {
	    NOTE,COMMENT,ATTACHMENT, DELIVERABLE, PROJECT
	}
	public static enum OperationType{
		ADD,REMOVE,UPDATE
	}
	private StepContentType order; 
	private OperationType operation;
	private Long authorId;
	private int groupId;
	private int groupStepId;
	private ProjectStepNote projectStepNote;
	private ProjectStepRemark projectStepRemark;
	private ProjectStepAttachment projectStepAttachment;
	private ProjectStepDeliverable projectStepDeliverable;
	private Project project;
	
	public StepContentType getOrder() {
		return order;
	}
	public void setOrder(StepContentType order) {
		this.order = order;
	}
	public ProjectStepNote getProjectStepNote() {
		return projectStepNote;
	}
	public void setProjectStepNote(ProjectStepNote projectStepNote) {
		this.projectStepNote = projectStepNote;
	}
	public ProjectStepRemark getProjectStepRemark() {
		return projectStepRemark;
	}
	public void setProjectStepRemark(ProjectStepRemark projectStepRemark) {
		this.projectStepRemark = projectStepRemark;
	}
	public ProjectStepAttachment getProjectStepAttachment() {
		return projectStepAttachment;
	}
	public void setProjectStepAttachment(ProjectStepAttachment projectStepAttachment) {
		this.projectStepAttachment = projectStepAttachment;
	}
	public Long getAuthorId() {
		return authorId;
	}
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	public OperationType getOperation() {
		return operation;
	}
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getGroupStepId() {
		return groupStepId;
	}
	public void setGroupStepId(int groupStepId) {
		this.groupStepId = groupStepId;
	}
	public ProjectStepDeliverable getProjectStepDeliverable() {
		return projectStepDeliverable;
	}
	public void setProjectStepDeliverable(ProjectStepDeliverable projectStepDeliverable) {
		this.projectStepDeliverable = projectStepDeliverable;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
}
