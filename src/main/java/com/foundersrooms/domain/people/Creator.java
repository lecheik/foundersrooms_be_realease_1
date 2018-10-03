package com.foundersrooms.domain.people;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersrooms.domain.project.Project;


@Entity
public class Creator extends User {
	
	private static final long serialVersionUID = 890345L;

	public Creator() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@OneToMany(mappedBy = "creator", cascade=CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Project> projectCreated=new HashSet<>(); 

}
