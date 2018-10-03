package com.foundersrooms.domain.project;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Step implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int stepNum;
	private String stepName;
	private String description = "";
	private boolean completed = false;	
	private int assignedTaskNumber=0;	
	private int completedTaskNumber=0;	
	private float taskRatio=0;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private Project projectReference;

	@OneToMany(mappedBy = "projectStep", cascade= {CascadeType.MERGE,CascadeType.REMOVE}, fetch = FetchType.EAGER)
	private Set<Task> stepTasks = new HashSet<>();

	@PrePersist
	@PreUpdate
	public void initStepItemBeforeLoad() {		
		/*assignedTaskNumber=stepTasks.size();
		completedTaskNumber=0;
		for(Task task:stepTasks) {
			if(task.isCompleted())
				completedTaskNumber=completedTaskNumber+1;			
		}
		if(assignedTaskNumber!=0)
			taskRatio=(completedTaskNumber/assignedTaskNumber)*100;
		else 
			taskRatio=0f;
		if(assignedTaskNumber==completedTaskNumber && assignedTaskNumber!=0)
			completed=true;*/
	}
	/*
	@PrePersist
	@PreUpdate	
	public void controlStepStateValue() {
		if(this.description.length()>1 || this.stepTasks.size()>0) {
			assignedTaskNumber=stepTasks.size();
			completedTaskNumber=0;
			for(int i=0;i<stepTasks.size();i++) {
				Task currentTask=(Task)stepTasks.toArray()[i];
				if(currentTask.isCompleted())
					completedTaskNumber=completedTaskNumber+1;
			}
			taskRatio=assignedTaskNumber/completedTaskNumber;
			if(completedTaskNumber==assignedTaskNumber) 
				completed=true;
				
		}else {this.completed=false;taskRatio=0;}
	}*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStepNum() {
		return stepNum;
	}

	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public float getTaskRatio() {
		return taskRatio;
	}

	public void setTaskRatio(float taskRatio) {
		this.taskRatio = taskRatio;
	}


	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Project getProjectReference() {
		return projectReference;
	}

	public void setProjectReference(Project projectReference) {
		this.projectReference = projectReference;
	}

	public Set<Task> getStepTasks() {
		return stepTasks;
	}

	public void setStepTasks(Set<Task> stepTasks) {
		this.stepTasks = stepTasks;
	}

	public int getAssignedTaskNumber() {
		return assignedTaskNumber;
	}

	public void setAssignedTaskNumber(int assignedTaskNumber) {
		this.assignedTaskNumber = assignedTaskNumber;
	}

	public int getCompletedTaskNumber() {
		return completedTaskNumber;
	}

	public void setCompletedTaskNumber(int completedTaskNumber) {
		this.completedTaskNumber = completedTaskNumber;
	}

}
