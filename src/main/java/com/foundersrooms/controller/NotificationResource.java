package com.foundersrooms.controller;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.NotificationWebSocketToken;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.UserElasticRepository;
import com.foundersrooms.service.NotificationService;
import com.foundersrooms.service.ProjectStepService;
import com.foundersrooms.service.UserService;
import com.foundersrooms.utility.MailConstructor;
import org.springframework.mail.SimpleMailMessage;

@RestController
@RequestMapping("/notification")
public class NotificationResource {

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private ProjectStepService projectStepService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;



	@RequestMapping(value = "/send/{id}", method = RequestMethod.POST)
	public ResponseEntity sendNotification(@RequestBody HashMap<String, Object> mapper,
			@PathVariable("id") Long userId,Principal principal) {
		User guest = userService.findOne(userId);
		int id = (Integer) mapper.get("id");
		User userLoggedIn = userService.findById(Long.valueOf(id));

		Notification notification = new Notification();
		notification.setGuest(guest);
		notification.setNotifier(userLoggedIn);
		notification.setPending(true);
		notification.setTimeOfOccurence(new Date());
		notification.setTimeOfAck(null);

		notification = notificationService.save(notification);

		notificationService.sendNotification(userLoggedIn, guest, notification);
		SimpleMailMessage newEmail = mailConstructor.constructNotificationRequest(guest, userLoggedIn);
		mailSender.send(newEmail);		

		return new ResponseEntity("Send notification Success", HttpStatus.OK);
	}

	
	@RequestMapping(value = "/cancel_relationship/{id}", method = RequestMethod.POST)
	public ResponseEntity cancelRelationShip(@RequestBody HashMap<String, Object> mapper,
			@PathVariable("id") Long userId) {

		User guest = userService.findOne(userId);
		int id = (Integer) mapper.get("id");
		User userLoggedIn = userService.findById(Long.valueOf(id));		
		
		notificationService.cancelRelationship(userLoggedIn, guest);	
		notificationService.notifyAllToRunMatchingAlgorithm(userLoggedIn.getUsername());
		//projectStepService.removeMember(userId,userLoggedIn.getId());
		return new ResponseEntity("Cancel relationship Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/accept/{id}", method = RequestMethod.POST)
	public ResponseEntity acceptNotification(@RequestBody HashMap<String, Object> mapper,
			@PathVariable("id") Long userId) {

		User notifier = userService.findOne(userId);
		int id = (Integer) mapper.get("id");
		User me = userService.findById(Long.valueOf(id));
		
		User elasticUserNotifier=userService.findElasticUserByUserName(notifier.getUsername());
		User elasticUserMe=userService.findElasticUserByUserName(me.getUsername());

		Notification notification = notificationService.findNotificationSendToMeByANotifier(me, notifier);
		
		notification.setTimeOfAck(new Date());
		notification.setPending(false);

		notifier.getContacts().add(me);
		me.getContacts().add(notifier);
		
		//align mysql contacts with elasticsearch contacts
		elasticUserNotifier.getElasticContacts().add(userService.buildElasticContact(notifier.getUsername(),me));
		elasticUserMe.getElasticContacts().add(userService.buildElasticContact(me.getUsername(),notifier));
		
		notificationService.save(notification);

		userService.save(notifier);
		userService.save(me);
		
		userService.elasticSave(elasticUserNotifier);userService.elasticSave(elasticUserMe);
		
		notificationService.notify(new NotificationWebSocketToken(), notifier.getUsername());
		notificationService.notifyAllToRunMatchingAlgorithm(me.getUsername());
		return new ResponseEntity("Accept notification Success", HttpStatus.OK);
	}

	@RequestMapping(value = "/invitations_items_number")
	public ResponseEntity getNumberInvitations(Principal principal) {
		User user = userService.findByUsername(principal.getName());
		return new ResponseEntity(user.getInvitations().size(), HttpStatus.OK);
	}

}
