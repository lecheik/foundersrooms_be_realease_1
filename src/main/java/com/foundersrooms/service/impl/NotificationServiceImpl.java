package com.foundersrooms.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.NotificationWebSocketToken;
import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.UserElasticRepository;
import com.foundersrooms.repository.NotificationRepository;
import com.foundersrooms.repository.ProjectMemberRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.NotificationService;
import com.foundersrooms.service.UserService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;	
	
	@Autowired
	private UserElasticRepository userElasticRepository;
	
	@Autowired
	private ProjectMemberRepository projectMemberRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactService contactService;

	@Override
	public List<Notification> findByGuestId(Long guestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Notification> findByNotifierId(Long notifierId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Notification save(Notification notification) {
		// TODO Auto-generated method stub
		return notificationRepository.save(notification);
	}

	@Override
	public Notification findById(Long id) {
		// TODO Auto-generated method stub
		return notificationRepository.findOne(id);
	}

	@Override
	public List<Notification> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Notification findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeOne(Long id) {
		// TODO Auto-generated method stub
		notificationRepository.delete(id);

	}

	@Override
	public void sendNotification(User notifier, User guest, Notification notification) {
		// TODO Auto-generated method stub
		notifier.getRequetes().add(notification);
		guest.getInvitations().add(notification);
		notifier=userRepository.save(notifier);
		guest=userRepository.save(guest);
		NotificationWebSocketToken token=new NotificationWebSocketToken(1,notifier.getId(),2);
		token.setUser(notifier);
		notify(token,guest.getUsername());
	}

	@Override
	public Boolean isNotificationAlreadySent(User notifier, User guest) {
		List<User> users = notificationRepository.findGuestByNotifier(notifier.getId(), guest.getId());
		if (!users.isEmpty())
			return true;
		return false;
	}

	@Transactional
	@Override
	public void cancelRelationship(User notifier, User guest) {
		User elasticNotifier=userElasticRepository.findByUsername(notifier.getUsername());
		User elasticGuest=userElasticRepository.findByUsername(guest.getUsername());
		/*List<Notification> notifications = notificationRepository
				.findGuestAndNotifierRelatedNotifications(notifier.getId(), guest.getId());*/
		//if (!notifications.isEmpty()) {
			/*
			for(Notification notification : notifications) {				
				notifier.getRequetes().remove(notification);
				notifier.getInvitations().remove(notification);
				guest.getInvitations().remove(notification);
				guest.getRequetes().remove(notification);
				notificationRepository.delete(notification);				
			}*/
			notificationRepository.deleteNotificationsRelatedToBothContacts(notifier.getId(), guest.getId());
			notifier.getContacts().remove(guest);
			guest.getContacts().remove(notifier);
			/*
			elasticNotifier.getElasticContacts().remove(userService.buildElasticContact(guest));
			elasticGuest.getElasticContacts().remove(userService.buildElasticContact(notifier));*/
			ElasticContact notifierContact=contactService.findByOwnerUsernameAndContactUsername(elasticNotifier.getUsername(), elasticGuest.getUsername());
			ElasticContact guestContact=contactService.findByOwnerUsernameAndContactUsername(elasticGuest.getUsername(), elasticNotifier.getUsername());
			
			elasticNotifier.getElasticContacts().remove(notifierContact);
			elasticGuest.getElasticContacts().remove(guestContact);
			
			contactService.removeOne(notifierContact);contactService.removeOne(guestContact);
			
			userRepository.save(notifier);
			userRepository.save(guest);					
			userElasticRepository.save(elasticNotifier);userElasticRepository.save(elasticGuest);
			//disable project member if exists
			//projectMemberRepository.disableOldContactFromMyProjects(notifier.getId(), guest.getId());
			Set<Long> pmIdRelatedToCurrentUserProjects=projectMemberRepository.findProjectMemberIdsRelatedToUserId(guest.getId(), notifier.getId());
			if(pmIdRelatedToCurrentUserProjects.size()>0)
				projectMemberRepository.disableOldContactFromMyProjects(pmIdRelatedToCurrentUserProjects);
		//}
		//fake object is pass as first parameter but will be updated later on
		notify(new NotificationWebSocketToken(),guest.getUsername());
		notify(new NotificationWebSocketToken(),notifier.getUsername());
		
	}

	@Override
	public boolean isConnectionNotificationReceived(User me, User notifier) {
		// TODO Auto-generated method stub
		List<Notification> notifications = notificationRepository.findNotificationReceivedFromNotifier(me.getId(),
				notifier.getId());
		if (!notifications.isEmpty())
			return true;
		return false;
	}

	@Override
	public Notification findNotificationSendToMeByANotifier(User me, User notifier) {
		// TODO Auto-generated method stub
		List<Notification> notifications = notificationRepository.findNotificationReceivedFromNotifier(me.getId(),
				notifier.getId());
		if (!notifications.isEmpty())
			return notifications.get(0);
		return null;
	}

	@Override
	public void acceptNotification(User me, User notifier) {								
		Notification notification = findNotificationSendToMeByANotifier(me,notifier);;
		notification.setTimeOfAck(new Date());
		notification.setPending(false);	

		notifier.getContacts().add(me);
		me.getContacts().add(notifier);

		save(notification);

		userRepository.save(notifier);
		userRepository.save(me);
		
	}

	public void notify(NotificationWebSocketToken token, String username) {
		messagingTemplate.convertAndSend("/topic/"+username, token);
		return;
	}

	@Override
	public void notifyAllToRunMatchingAlgorithm(String notifierUsername) {
		// TODO Auto-generated method stub
		messagingTemplate.convertAndSend("/topic/matching", notifierUsername);
		
	}

}
