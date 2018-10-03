package com.foundersrooms.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.service.SearchService;
import com.foundersrooms.service.UserService;
import com.google.gson.Gson;

@RestController
@RequestMapping("/search")
public class SearchResource {
	
	Gson gson=new Gson();

	@Autowired
	private SearchService searchService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/users")
	public ResponseEntity searchUsers(@RequestBody String userParameters,Principal principal) {		
		User user=gson.fromJson(userParameters, User.class);
		User me=userService.findByUsername(principal.getName());
		return new ResponseEntity(searchService.searchUsers(user,me),HttpStatus.OK);
		//return new ResponseEntity(searchService.searchElasticUsers(user,me),HttpStatus.OK);
	}
	
	@RequestMapping(value="/users/name_like")
	public ResponseEntity searchUsersWhoseNameLike(@RequestBody String payLoad,Principal principal) {		
		payLoad=payLoad.trim();
		//User me=userService.findByUsername(principal.getName());
		List<ElasticContact> result=searchService.searchContactsWhoseCompleteNameStartsWith(payLoad,principal.getName());
		//result.remove(me);
		return new ResponseEntity(result,HttpStatus.OK);
	}	
	
	@RequestMapping(value="/projects")
	public ResponseEntity searchProjects(@RequestBody String projectParameters,Principal principal) {		
		Project project=gson.fromJson(projectParameters, Project.class);
		User me=userService.findByUsername(principal.getName());
		return new ResponseEntity(searchService.searchProjects(project),HttpStatus.OK);
	}	
	
	@RequestMapping(value="/matching")
	public ResponseEntity runMatching(Principal principal) {		
		User user=userService.findElasticUserByUserName(principal.getName());		
		List<User> result=searchService.getUserProfileMatching(user);
		return new ResponseEntity(result,HttpStatus.OK);
	}	
	
}
