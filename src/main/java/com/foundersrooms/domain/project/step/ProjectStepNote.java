package com.foundersrooms.domain.project.step;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ProjectStepNote implements Serializable{

	/**
	 * 
	 */
	public static enum NoteType {MINOR,MAJOR,IMPORTANT};
	private static final long serialVersionUID = -3596209138585462308L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Lob
	private String content="";
	
	private Date creationDate;
	
	private Date lastModidicationDate;	
	
	private NoteType noteType=NoteType.MINOR;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worflow_step_id")
	@JsonIgnore	
	private ProjectWorkflowStep worflowStepReference;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NoteType getNoteType() {
		return noteType;
	}
	public void setNoteType(NoteType noteType) {
		this.noteType = noteType;
	}
	public ProjectWorkflowStep getWorflowStepReference() {
		return worflowStepReference;
	}
	public void setWorflowStepReference(ProjectWorkflowStep worflowStepReference) {
		this.worflowStepReference = worflowStepReference;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ProjectStepNote other = (ProjectStepNote) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
