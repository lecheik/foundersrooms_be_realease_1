package com.foundersrooms.service;

import java.util.List;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.NotificationWebSocketToken;
import com.foundersrooms.domain.people.User;;

public interface NotificationService {
		
	List<Notification> findByGuestId(Long guestId);
	
	List<Notification> findByNotifierId(Long notifierId);	
	
	Notification save(Notification notification);
	
	Notification findById(Long id);
	
	List<Notification> findAll();
	
	Notification findOne(Long id);
	
	void removeOne(Long id);
	
	Notification findNotificationSendToMeByANotifier(User me, User notifier);
	
	Boolean isNotificationAlreadySent(User notifier, User guest);
	
	boolean isConnectionNotificationReceived(User me, User notifier);
	
	void sendNotification(User notifier, User guest, Notification notification);
	
	void cancelRelationship(User notifier, User guest);
	
	void acceptNotification(User guest, User notifier);
	
	public void notify(NotificationWebSocketToken token, String username) ;
	
	public void notifyAllToRunMatchingAlgorithm(String notifierUsername);
}
