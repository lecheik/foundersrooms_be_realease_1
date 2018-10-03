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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.step.ProjectStepAttachment;
import com.foundersrooms.domain.project.step.ProjectStepRemark;

@Entity
public class ProjectMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id")
	//@JsonIgnore
	private User member;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private Project project;
	
	private boolean activeMember=true;
	
	@OneToMany(mappedBy="assignedTo", cascade= {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Task> projectMemberTasks=new HashSet<>();

	@OneToMany(mappedBy="author", cascade= {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JsonIgnore	
	private Set<ProjectStepAttachment> projectMemberAttachment=new HashSet<>();
	
	@OneToMany(mappedBy="author", cascade= {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JsonIgnore	
	private Set<ProjectStepRemark> projectMemberRemarks=new HashSet<>();	
	
	private String scope;

	public ProjectMember() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getMember() {
		return member;
	}

	public void setMember(User member) {
		this.member = member;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Set<Task> getProjectMemberTasks() {
		return projectMemberTasks;
	}

	public void setProjectMemberTasks(Set<Task> projectMemberTasks) {
		this.projectMemberTasks = projectMemberTasks;
	}

	public boolean isActiveMember() {
		return activeMember;
	}

	public void setActiveMember(boolean activeMember) {
		this.activeMember = activeMember;
	}

	public Set<ProjectStepRemark> getProjectMemberRemarks() {
		return projectMemberRemarks;
	}

	public void setProjectMemberRemarks(Set<ProjectStepRemark> projectMemberRemarks) {
		this.projectMemberRemarks = projectMemberRemarks;
	}

	public Set<ProjectStepAttachment> getProjectMemberAttachment() {
		return projectMemberAttachment;
	}

	public void setProjectMemberAttachment(Set<ProjectStepAttachment> projectMemberAttachment) {
		this.projectMemberAttachment = projectMemberAttachment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((member == null) ? 0 : member.hashCode());
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
		ProjectMember other = (ProjectMember) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		return true;
	}

}
