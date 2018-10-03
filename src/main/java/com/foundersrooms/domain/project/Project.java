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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

//import org.springframework.data.elasticsearch.annotations.Document;

import com.foundersrooms.domain.people.Creator;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.step.ProjectWorkflowStep;
import com.foundersrooms.service.impl.UtilityServiceImpl;


@Entity
@Document(indexName = "foundersrooms", type = "projects")
public class Project implements Serializable{
	/**
	 * 
	 */	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private static final long serialVersionUID = 888952201L;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	@Lob
	private String description;
	
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String name;
	
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String nameSlug;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String videoPitch;
	
	@Field(type = FieldType.Boolean, index = FieldIndex.not_analyzed, store = true)
	private boolean archived;
	
	@Field(type = FieldType.Float, index = FieldIndex.not_analyzed, store = true)
	private float ratio=0f;
	
	@Transient
	private int numberOfAssignedTasks=0;
	
	@Transient
	private int numberOfCompletedTask=0;
	
	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int currentStepNum;
	
	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int teamSize;
	
	@Transient
	private int teamSizeSearchInf;
	@Transient
	private int teamSizeSearchSup;
	@Transient
	private int stepSearchInf;
	@Transient
	private int stepSearchSup;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "creator_id")
	@Field(type = FieldType.Nested)
	private User creator;
	
	private String creatorTown;
	
	@OneToMany(mappedBy="projectReference", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Step> projectSteps=new HashSet<>();
	
	@OneToMany(mappedBy="projectReference", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ProjectWorkflowStep> projectWfSteps=new HashSet<>();	
	
	@OneToMany(mappedBy="project", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectMember> members=new HashSet<>();
	
	@Transient
	private Set<User> userMembers=new HashSet<>();
	
	@OneToMany(mappedBy="project", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectAdvice> advices=new HashSet<>();
	
	@OneToMany(mappedBy="project", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectCoaching> coachings=new HashSet<>();

	@OneToMany(mappedBy="project", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectInvestmentAndFinance> invesments=new HashSet<>();
	
	public Project() {
		super();
		// TODO Auto-generated constructor stub
	}

	@PostLoad
	public void postloadInitialization() {
		for(ProjectMember pm:members)
			userMembers.add(pm.getMember());
		/*
		float completedSteps=0f;
		for(Step step:projectSteps) {
			numberOfAssignedTasks=numberOfAssignedTasks+step.getAssignedTaskNumber();
			numberOfCompletedTask=numberOfCompletedTask+step.getCompletedTaskNumber();
			if(step.isCompleted()) completedSteps=completedSteps+1;			
		}
		ratio=(completedSteps/10f)*100f;*/
		for(ProjectWorkflowStep pws:projectWfSteps)
			ratio=ratio+pws.getStepRatio();
		//static assigment, will be updated later
		ratio=ratio/13f;
	}
	
	@PrePersist
	@PreUpdate
	public void setControlValues() {
		teamSize=members.size();
		currentStepNum=0;
		for(Step step:projectSteps) 
			if(step.getStepNum()>currentStepNum)
				currentStepNum=step.getStepNum();	
		nameSlug=UtilityServiceImpl.toSlug(name.toLowerCase());
		creatorTown=UtilityServiceImpl.toSlug(creator.getTown().toLowerCase());
	}
	

	public String getNameSlug() {
		return nameSlug;
	}

	public void setNameSlug(String nameSlug) {
		this.nameSlug = nameSlug;
	}

	public int getTeamSizeSearchInf() {
		return teamSizeSearchInf;
	}

	public void setTeamSizeSearchInf(int teamSizeSearchInf) {
		this.teamSizeSearchInf = teamSizeSearchInf;
	}

	public int getTeamSizeSearchSup() {
		return teamSizeSearchSup;
	}

	public void setTeamSizeSearchSup(int teamSizeSearchSup) {
		this.teamSizeSearchSup = teamSizeSearchSup;
	}

	public int getStepSearchInf() {
		return stepSearchInf;
	}

	public void setStepSearchInf(int stepSearchInf) {
		this.stepSearchInf = stepSearchInf;
	}

	public int getStepSearchSup() {
		return stepSearchSup;
	}

	public void setStepSearchSup(int stepSearchSup) {
		this.stepSearchSup = stepSearchSup;
	}

	public int getCurrentStepNum() {
		return currentStepNum;
	}

	public void setCurrentStepNum(int currentStepNum) {
		this.currentStepNum = currentStepNum;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getRatio() {
		return ratio;
	}


	public void setRatio(float ratio) {
		this.ratio = ratio;
	}


	public String getVideoPitch() {
		return videoPitch;
	}


	public void setVideoPitch(String videoPitch) {
		this.videoPitch = videoPitch;
	}


	public boolean isArchived() {
		return archived;
	}


	public void setArchived(boolean archived) {
		this.archived = archived;
	}


	public User getCreator() {
		return creator;
	}


	public void setCreator(Creator creator) {
		this.creator = creator;
	}


	public Set<Step> getProjectSteps() {
		return projectSteps;
	}

	public void setProjectSteps(Set<Step> projectSteps) {
		this.projectSteps = projectSteps;
	}

	public Set<ProjectMember> getMembers() {
		return members;
	}


	public Set<User> getUserMembers() {
		return userMembers;
	}


	public void setUserMembers(Set<User> userMembers) {
		this.userMembers = userMembers;
	}


	public void setMembers(Set<ProjectMember> members) {
		this.members = members;
	}

	public int getNumberOfAssignedTasks() {
		return numberOfAssignedTasks;
	}

	public void setNumberOfAssignedTasks(int numberOfAssignedTasks) {
		this.numberOfAssignedTasks = numberOfAssignedTasks;
	}

	public int getNumberOfCompletedTask() {
		return numberOfCompletedTask;
	}

	public void setNumberOfCompletedTask(int numberOfCompletedTask) {
		this.numberOfCompletedTask = numberOfCompletedTask;
	}

	public Set<ProjectWorkflowStep> getProjectWfSteps() {
		return projectWfSteps;
	}

	public void setProjectWfSteps(Set<ProjectWorkflowStep> projectWfSteps) {
		this.projectWfSteps = projectWfSteps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nameSlug == null) ? 0 : nameSlug.hashCode());
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
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameSlug == null) {
			if (other.nameSlug != null)
				return false;
		} else if (!nameSlug.equals(other.nameSlug))
			return false;
		return true;
	} 
	
	

}
