package com.foundersrooms.service;

import java.util.List;
import java.util.Set;

import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;

public interface SearchService {
	Set<User> searchUsers(User userTemplate,User me);
	
	Set<User> searchElasticUsers(User userTemplate,User me);
	
	Set<Project> searchProjects(Project projectTemplate);
	
	Set<Project> searchElasticProjects(Project projectTemplate);
	
	List<User> getUserProfileMatching(User user);
	
	List<ElasticContact> searchContactsWhoseCompleteNameStartsWith(String payLoad,String loggedInUsername);
}
