package com.foundersrooms.service;

import com.foundersrooms.domain.messenger.Chat;
import com.foundersrooms.domain.messenger.Message;
import com.foundersrooms.domain.people.User;

import java.util.Set;

public interface ChatService {
	
	Chat saveChat(Chat chat);
	
	Set<Chat> loadWholeExchanges(User user);
	
	Set<Message> loadChatMessages(String userLoggedInUsername,String correspondantUsername);
	
	Set<Message> loadMySQLChatMessages(String userLoggedInUsername,String correspondantUsername);
	
	Chat loadSpecificChat(String currentLoggedInUsername, String correspondantUsername);
	
	Chat createChat(User owner,User correspondant);
	
	Message createChatMessage(String ownerUsername,String correspondantUsername, String messageContent);
	
	Message acknowledgeMessage(String ownerUsername,String correspondantUsername,String messageId);
	
	void notifyCorrespondant(User correspondant, Message message, Chat chat);
	
	void notifyAcknowledgement(String correspondantUsername, String messageId);
	
	void sendKeepAliveToMyContacts(User user, boolean status);
	
	boolean isChatExists(String userLoggedInUsername,String correspondantUsername);
	
	void ackAllMessagesExchangedWithACorrespondant(String userLoggedInUsername,String correspondantUsername);
	
	long countUnreadMessages(String username);
	
	void clearAllChatsAndMessages();
}
