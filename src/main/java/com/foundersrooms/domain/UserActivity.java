package com.foundersrooms.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.foundersrooms.domain.people.User;

@Entity
public class UserActivity implements Serializable{
	/**
	 * 
	 */

	private static final long serialVersionUID = 890345L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long userSectorID;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	private Activity secteur;

	public UserActivity() {

	}

}
