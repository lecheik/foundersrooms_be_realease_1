package com.foundersrooms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.people.User;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

	
	@Query("SELECT n.guest FROM Notification n WHERE n.notifier.id=:notifierId AND n.guest.id=:guestId" )
	List<User> findGuestByNotifier(@Param("notifierId") Long notifierId,@Param("guestId") Long guestId);
	
	
	@Query("SELECT n FROM Notification n WHERE n.notifier.id=:notifierId AND n.guest.id=:guestId" )
	List<Notification> findNotificationSentByNotifierToGuest(@Param("notifierId") Long notifierId,@Param("guestId") Long guestId);
	
	@Query("SELECT n FROM Notification n WHERE n.notifier.id=:notifierId AND n.guest.id=:myId" )
	List<Notification> findNotificationReceivedFromNotifier(@Param("myId") Long myId,@Param("notifierId") Long notifierId);
	
	@Query("SELECT DISTINCT n FROM Notification n WHERE (n.notifier.id=:notifierId AND n.guest.id=:myId) OR (n.notifier.id=:myId AND n.guest.id=:notifierId)")
	List<Notification> findGuestAndNotifierRelatedNotifications(@Param("myId") Long myId,@Param("notifierId") Long notifierId);
	
	@Query("DELETE FROM Notification where id=:notificationId")
	void deleteNotification(@Param("notificationId") Long notificationId);
	
	@Modifying
	@Query("DELETE FROM Notification n WHERE (n.notifier.id=:notifierId AND n.guest.id=:myId) OR (n.notifier.id=:myId AND n.guest.id=:notifierId)")
	void deleteNotificationsRelatedToBothContacts(@Param("myId") Long myId,@Param("notifierId") Long notifierId);
}
