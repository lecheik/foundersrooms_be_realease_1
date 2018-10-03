package com.foundersrooms.domain.project;

import javax.persistence.Entity;

@Entity
public class Advice extends Need {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
	@OneToMany(mappedBy="advice", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectAdvice> projectAdvices=new HashSet<>();
	
	*/
	public Advice() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
