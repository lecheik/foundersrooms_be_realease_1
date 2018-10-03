package com.foundersrooms.domain.project.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersrooms.domain.project.Project;

@Entity
public class ProjectWorkflowStep implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3867370146091146711L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private Project projectReference;
	
	private String groupName="";
	
	private int groupId;
	
	private int groupStepId;
	
	private int stepNum;
	
	private boolean completed = false;
	
	private String stepName;
	
	private float stepRatio;
	
	@Lob
	private String descrip="";
	@Transient
	private List<String> description=new ArrayList<String>();
	//deliverables raw data
	@Lob
	private String del="";
	@Transient
	private List<String> deliverables=new ArrayList<String>();
	
	//resources raw data
	@Lob
	private String res="";
	@Transient
	private List<String> resources=new ArrayList<String>();
	
	//recommandations raw data
	@Lob
	private String recom="";
	@Transient
	private List<String> recommandations=new ArrayList<String>();
	
	// links raw data
	@Lob
	private String links;
	@Transient
	private List<String> liens=new ArrayList<String>();
	
	@OneToMany(mappedBy="worflowStepReference", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("lastModidicationDate DESC")
	private Set<ProjectStepNote> notes=new HashSet();
	
	@OneToMany(mappedBy="worflowStepReference", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("creationDate DESC")
	private Set<ProjectStepRemark> remarks=new HashSet();
	
	
	@OneToMany(mappedBy="worflowStepReference", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("uploadDate ASC")
	private Set<ProjectStepAttachment> attachmentsMetaData=new HashSet();
	
	@OneToMany(mappedBy="worflowStepReference", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("id ASC")
	private Set<ProjectStepDeliverable> deliverablesModelStatus=new HashSet();	
	
	@PrePersist
	@PreUpdate
	@PostLoad
	void callBackMethod() {
		float compteur=0f;
		for(int i=0;i<deliverablesModelStatus.size();i++)
			compteur=compteur+((ProjectStepDeliverable)deliverablesModelStatus.toArray()[i]).getProgress();
		if(deliverablesModelStatus.size()>0)
			this.stepRatio=compteur/deliverablesModelStatus.size();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	public String getDesc() {
		return descrip;
	}
	public void setDesc(String desc) {
		this.descrip = desc;
	}
	public List<String> getDescription() {
		return description;
	}
	public void setDescription(List<String> description) {
		this.description = description;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	public List<String> getDeliverables() {
		return deliverables;
	}
	public void setDeliverables(List<String> deliverables) {
		this.deliverables = deliverables;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	public String getRecom() {
		return recom;
	}
	public void setRecom(String recom) {
		this.recom = recom;
	}
	public List<String> getRecommandations() {
		return recommandations;
	}
	public void setRecommandations(List<String> recommandations) {
		this.recommandations = recommandations;
	}
	public String getLinks() {
		return links;
	}
	public void setLinks(String links) {
		this.links = links;
	}
	public List<String> getLiens() {
		return liens;
	}
	public void setLiens(List<String> liens) {
		this.liens = liens;
	}
	public Project getProjectReference() {
		return projectReference;
	}
	public void setProjectReference(Project projectReference) {
		this.projectReference = projectReference;
	}
	public Set<ProjectStepNote> getNotes() {
		return notes;
	}
	public void setNotes(Set<ProjectStepNote> notes) {
		this.notes = notes;
	}
	public Set<ProjectStepRemark> getRemarks() {
		return remarks;
	}
	public void setRemarks(Set<ProjectStepRemark> remarks) {
		this.remarks = remarks;
	}
	public Set<ProjectStepAttachment> getAttachmentsMetaData() {
		return attachmentsMetaData;
	}
	public void setAttachmentsMetaData(Set<ProjectStepAttachment> attachmentsMetaData) {
		this.attachmentsMetaData = attachmentsMetaData;
	}	
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getDescrip() {
		return descrip;
	}
	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}	
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public int getGroupStepId() {
		return groupStepId;
	}
	public void setGroupStepId(int groupStepId) {
		this.groupStepId = groupStepId;
	}
	
	public Set<ProjectStepDeliverable> getDeliverablesModelStatus() {
		return deliverablesModelStatus;
	}
	public void setDeliverablesModelStatus(Set<ProjectStepDeliverable> deliverablesModelStatus) {
		this.deliverablesModelStatus = deliverablesModelStatus;
	}
	
	
	public float getStepRatio() {
		return stepRatio;
	}
	public void setStepRatio(float stepRatio) {
		this.stepRatio = stepRatio;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((stepName == null) ? 0 : stepName.hashCode());
		result = prime * result + stepNum;
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
		ProjectWorkflowStep other = (ProjectWorkflowStep) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (stepName == null) {
			if (other.stepName != null)
				return false;
		} else if (!stepName.equals(other.stepName))
			return false;
		if (stepNum != other.stepNum)
			return false;
		return true;
	}
	
}
