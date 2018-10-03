package com.foundersrooms.domain;

import java.util.HashSet;
import java.util.Set;

import com.foundersrooms.domain.people.User;

public class UserWrapperForNetwork{

	
	
	public UserWrapperForNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}
	private Set<User> contacts=new HashSet<>();
	private Set<User> usersInvited=new HashSet<>();
	private Set<User> userRequests=new HashSet<>();
	
	public Set<User> getUsersInvited() {
		return usersInvited;
	}
	public void setUsersInvited(Set<User> usersInvited) {
		this.usersInvited = usersInvited;
	}
	public Set<User> getUserRequests() {
		return userRequests;
	}
	public void setUserRequests(Set<User> userRequests) {
		this.userRequests = userRequests;
	}
	public Set<User> getContacts() {
		return contacts;
	}
	public void setContacts(Set<User> contacts) {
		this.contacts = contacts;
	}
	
}
