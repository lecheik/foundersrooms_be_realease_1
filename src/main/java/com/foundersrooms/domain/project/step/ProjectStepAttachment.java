package com.foundersrooms.domain.project.step;

import static com.foundersrooms.service.impl.UtilityServiceImpl.toSlug;

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
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;

@Entity
public class ProjectStepAttachment implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String attachmentName = "";
	private float attachmentSize = 0f;
	private String attachmentFileType = "";
	private Date uploadDate;
	private String s3Prefix="";
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
		userReference = author.getMember();
		Project project=getWorflowStepReference().getProjectReference();
		User projectAuthor=project.getCreator();
		s3Prefix=toSlug(projectAuthor.getUsername())+"/"+toSlug(project.getName())+"/"+getWorflowStepReference().getStepNum()+"/"+getId()+"/"+getAttachmentName() ;		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public float getAttachmentSize() {
		return attachmentSize;
	}

	public void setAttachmentSize(float attachmentSize) {
		this.attachmentSize = attachmentSize;
	}

	public String getAttachmentFileType() {
		return attachmentFileType;
	}

	public void setAttachmentFileType(String attachmentFileType) {
		this.attachmentFileType = attachmentFileType;
	}

	public ProjectWorkflowStep getWorflowStepReference() {
		return worflowStepReference;
	}

	public void setWorflowStepReference(ProjectWorkflowStep worflowStepReference) {
		this.worflowStepReference = worflowStepReference;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public ProjectMember getAuthor() {
		return author;
	}

	public void setAuthor(ProjectMember author) {
		this.author = author;
	}

	public User getUserReference() {
		return userReference;
	}

	public void setUserReference(User userReference) {
		this.userReference = userReference;
	}

	public String getS3Prefix() {
		return s3Prefix;
	}

	public void setS3Prefix(String s3Prefix) {
		this.s3Prefix = s3Prefix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachmentFileType == null) ? 0 : attachmentFileType.hashCode());
		result = prime * result + ((attachmentName == null) ? 0 : attachmentName.hashCode());
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
		ProjectStepAttachment other = (ProjectStepAttachment) obj;
		if (attachmentFileType == null) {
			if (other.attachmentFileType != null)
				return false;
		} else if (!attachmentFileType.equals(other.attachmentFileType))
			return false;
		if (attachmentName == null) {
			if (other.attachmentName != null)
				return false;
		} else if (!attachmentName.equals(other.attachmentName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
