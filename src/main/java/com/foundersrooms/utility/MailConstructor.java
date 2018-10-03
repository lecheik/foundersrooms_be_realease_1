package com.foundersrooms.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.foundersrooms.domain.people.User;

@Component
public class MailConstructor {

	@Autowired
	private Environment env;
	
	public SimpleMailMessage constructNewUserEmail(User user, String password, boolean newUser) {
		String message="\nPlease use the following credentials to log in and edit your personal information including your own password."
				+ "\nUsername:"+user.getUsername()+"\nPassword:"+password;
		
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		String subject="";
		if(newUser)
			subject="Founders Rooms - New User";
		else
			subject="Founders Rooms - Password Reset";
		email.setSubject(subject);
		email.setText(message);
		email.setFrom(env.getProperty("support.email"));
		return email;
	}
	
	public SimpleMailMessage constructNotificationRequest(User guest, User notifier) {
		String message="\nYou received connection request from "+ notifier.getUsername();				
		
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(guest.getEmail());
		email.setSubject("Founders Rooms - Connection Request");
		email.setText(message);
		email.setFrom(env.getProperty("support.email"));
		return email;
	}
}
