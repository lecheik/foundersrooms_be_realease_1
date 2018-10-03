package com.foundersrooms.domain.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.foundersrooms.domain.people.ServiceField;

@Entity
public class ProjectAdvice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4074114361919777583L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;
	
	@Embedded
	private ServiceField fields;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="project_id")
	private Project project;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="advice_id")
	private Advice advice;
	
	private int dailyRate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Advice getAdvice() {
		return advice;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public int getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(int dailyRate) {
		this.dailyRate = dailyRate;
	}	
	
}
