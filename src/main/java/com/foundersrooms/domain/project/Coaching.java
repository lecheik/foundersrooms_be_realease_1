package com.foundersrooms.domain.project;

import javax.persistence.Entity;

@Entity
public class Coaching  extends Need{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	@OneToMany(mappedBy="coaching", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectCoaching> projectCoachings=new HashSet<>();
*/
	public Coaching() {
		super();
		// TODO Auto-generated constructor stub
	}


	
	
}
