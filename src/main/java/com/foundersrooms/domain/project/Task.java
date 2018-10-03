package com.foundersrooms.domain.project;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersrooms.domain.people.User;


@Entity
public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String taskName;
	
	private boolean completed=false;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="assigned_to")
	//@JsonIgnore
	private ProjectMember assignedTo;
	
	@Transient
	private User userReference;//only to match dynamically new created user on incomming requests
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="project_step_id")
	@JsonIgnore
	private Step projectStep;
	
	@PostLoad
	public void initItem() {
		if(assignedTo!=null)
			userReference=assignedTo.getMember();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	
	public User getUserReference() {
		return userReference;
	}
	public void setUserReference(User userReference) {
		this.userReference = userReference;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public ProjectMember getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(ProjectMember assignedTo) {
		this.assignedTo = assignedTo;
	}
	public Step getProjectStep() {
		return projectStep;
	}
	public void setProjectStep(Step projectStep) {
		this.projectStep = projectStep;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (taskName == null) {
			if (other.taskName != null)
				return false;
		} else if (!taskName.equals(other.taskName))
			return false;
		return true;
	}
	
	
}
