package com.foundersrooms.service.impl;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.ContactRepository;
import com.foundersrooms.service.ChatService;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.UserService;
import com.foundersrooms.service.impl.ChatServiceImpl.KeepAliveMessage;
import com.google.gson.Gson;

@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	private Gson gson=new Gson();
	
	@Override
	public ElasticContact findByOwnerUsernameAndContactUsername(String ownerUsername, String contactUsername) {
		// TODO Auto-generated method stub
		return contactRepository.findByOwnerUsernameAndUsername(ownerUsername, contactUsername);
	}

	@Transactional
	@Override
	public void removeOne(String contactId) {
		// TODO Auto-generated method stub
		contactRepository.delete(contactId);
	}
	
	@Transactional
	@Override
	public void removeOne(ElasticContact contact) {
		// TODO Auto-generated method stub
		contactRepository.delete(contact);
	}
	
	class KeepAliveMessage{
		KeepAliveMessage(String _username, boolean _status){
			username=_username;
			status=_status;
		}
		String username;
		boolean status;
	}

	@Override
	public void updateOnlineStatus(String username, boolean status) {
		// TODO Auto-generated method stub
		User user=userService.findElasticUserByUserName(username);
		user.setOnline(status);
		userService.elasticSave(user);
		Set<ElasticContact> myReferencesInContacts=contactRepository.findByUsername(username);
		for(ElasticContact item:myReferencesInContacts) {
			item.setOnline(status);
			contactRepository.save(item);					
		}
	}

	@Override
	public void updateContactsFollowingUserProfileChange(User user) {
		// TODO Auto-generated method stub
		Set<ElasticContact> contacts=contactRepository.findByUsername(user.getUsername());
		for(ElasticContact item:contacts) {
			item.setLastName(user.getLastName());
			item.setFirstName(user.getFirstName());
			item.setCompleteName(user.getCompleteName());
			contactRepository.save(item);
		}
	}

	@Transactional
	@Override
	public void clearAllContacts() {
		// TODO Auto-generated method stub
		contactRepository.deleteAll();
	}	

}
