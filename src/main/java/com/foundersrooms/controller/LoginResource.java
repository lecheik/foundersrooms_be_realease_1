package com.foundersrooms.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.service.ChatService;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.UserService;

@RestController
public class LoginResource {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private ChatService chatService;
	
	@RequestMapping("/token")
	public Map<String,String> token(HttpSession session, HttpServletRequest request){
		System.out.println(request.getRemoteHost());
		String remoteHost=request.getRemoteHost();
		int portNumber=request.getRemotePort();
		
		System.out.println(remoteHost+": "+portNumber);
		System.out.println(request.getRemoteAddr());		
		return Collections.singletonMap("token", session.getId());
	}
	
	@RequestMapping("/checkSession")
	public ResponseEntity checkSession(Principal principal){
		//contactService.updateOnlineStatus(principal.getName(), true);
		chatService.sendKeepAliveToMyContacts(userService.findElasticUserByUserName(principal.getName()), true);
		return new ResponseEntity("Session Active!", HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/logout", method=RequestMethod.POST)
	public ResponseEntity logout(Principal principal){
		//contactService.updateOnlineStatus(principal.getName(), false);	
		chatService.sendKeepAliveToMyContacts(userService.findElasticUserByUserName(principal.getName()), false);
		SecurityContextHolder.clearContext();
		return new ResponseEntity("Logout Successfully!", HttpStatus.OK);
	}
	
}
