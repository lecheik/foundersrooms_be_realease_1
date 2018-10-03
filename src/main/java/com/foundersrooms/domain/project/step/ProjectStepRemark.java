package com.foundersrooms.domain.project.step;

import java.io.Serializable;
import java.util.Date;

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
import com.foundersrooms.domain.project.ProjectMember;

@Entity
public class ProjectStepRemark implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8524003671366582973L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String content;
	
	private Date creationDate;
	
	private Date lastModidicationDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_member_id")
	@JsonIgnore	
	private ProjectMember author;
	
	@Transient
	private User userReference;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worflow_step_id")
	@JsonIgnore	
	private ProjectWorkflowStep worflowStepReference;	
	
	@PostLoad
	public void callback() {
		userReference=author.getMember();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastModidicationDate() {
		return lastModidicationDate;
	}
	public void setLastModidicationDate(Date lastModidicationDate) {
		this.lastModidicationDate = lastModidicationDate;
	}
	public ProjectMember getAuthor() {
		return author;
	}
	public void setAuthor(ProjectMember author) {
		this.author = author;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public ProjectWorkflowStep getWorflowStepReference() {
		return worflowStepReference;
	}
	public void setWorflowStepReference(ProjectWorkflowStep worflowStepReference) {
		this.worflowStepReference = worflowStepReference;
	}
	
	public User getUserReference() {
		return userReference;
	}
	public void setUserReference(User userReference) {
		this.userReference = userReference;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ProjectStepRemark other = (ProjectStepRemark) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
