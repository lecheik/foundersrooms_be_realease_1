package com.foundersrooms.domain.project.step;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ProjectStepDeliverable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2027269469521922291L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private int deliverableNum;
	
	private String label;
	
	private float progress;
	
	private boolean completed=false;
	
	
	private String labelSlug;
	
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public ProjectWorkflowStep getWorflowStepReference() {
		return worflowStepReference;
	}

	public void setWorflowStepReference(ProjectWorkflowStep worflowStepReference) {
		this.worflowStepReference = worflowStepReference;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	
	
	public String getLabelSlug() {
		return labelSlug;
	}

	public void setLabelSlug(String labelSlug) {
		this.labelSlug = labelSlug;
	}
	
	

	public int getDeliverableNum() {
		return deliverableNum;
	}

	public void setDeliverableNum(int deliverableNum) {
		this.deliverableNum = deliverableNum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		ProjectStepDeliverable other = (ProjectStepDeliverable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}	
	
	
}
