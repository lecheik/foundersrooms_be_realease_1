package com.foundersrooms.service;

import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;

public interface ContactService {
	ElasticContact findByOwnerUsernameAndContactUsername(String ownerUsername,String contactUsername);
	
	void removeOne(String contactId);
	
	void removeOne(ElasticContact contact);
	
	void updateOnlineStatus(String username, boolean status);
	
	void updateContactsFollowingUserProfileChange(User user);
	
	void clearAllContacts();
}
