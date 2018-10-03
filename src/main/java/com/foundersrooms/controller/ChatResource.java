package com.foundersrooms.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cloudfront.model.Method;
import com.foundersrooms.domain.messenger.Chat;
import com.foundersrooms.domain.messenger.Message;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.service.ChatService;
import com.foundersrooms.service.UserService;
import com.google.gson.Gson;

@RestController
@RequestMapping("/chat")
public class ChatResource {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatService chatService;
	
	private Gson gson=new Gson();
	
	class MessageAdapter2 extends Message{
		MessageAdapter2(Message message){
			this.setId(message.getId());
			this.setAcknowledged(message.isAcknowledged());
			this.setContent(message.getContent());
			this.setCorrespondantUsername(message.getCorrespondantUsername());
			this.setOrderField(message.getOrderField());
			this.setOwnerUsername(message.getOwnerUsername());
			this.setReceivedDate(message.getReceivedDate());
			this.setSendTime(message.getSendTime());				
		}
		String correspondantFirstName;
		String completeCorrespondantName;
		Long correspondantId;
		Date lastChatActivity;
	}
	
	@RequestMapping(value="/load_whole_exchanges")
	public ResponseEntity loadWholeExchanges(Principal principal) {
		User userLoggedIn=userService.findElasticUserByUserName(principal.getName());		
		return new ResponseEntity(chatService.loadWholeExchanges(userLoggedIn),HttpStatus.OK);
	}
	
	private class ActiveChatTemplate{
		User correspondant;
		Set<Message> messages;
	}
	
	@RequestMapping(value="/load_chat_messages")
	public ResponseEntity loadChatMessages(@RequestBody String correspondantUsername,Principal principal) {
		User correspondant=userService.findElasticUserByUserName(correspondantUsername);	
		ActiveChatTemplate activeChat=new ActiveChatTemplate();
		//activeChat.messages=chatService.loadChatMessages(principal.getName(), correspondant.getUsername());
		activeChat.messages=chatService.loadMySQLChatMessages(principal.getName(), correspondant.getUsername());
		activeChat.correspondant=correspondant;
		chatService.ackAllMessagesExchangedWithACorrespondant(principal.getName(),correspondant.getUsername());
		return new ResponseEntity(gson.toJson(activeChat),HttpStatus.OK);
	}
	
	private class MessageAdapter{
		String correspondantUsername;
		String messageContent;
	}
	
	@RequestMapping(value="/send_message", method=RequestMethod.POST)
	public ResponseEntity sendMessage(Principal principal,@RequestBody String postContent) {
		MessageAdapter messageAdapter=gson.fromJson(postContent, MessageAdapter.class);
		User currentUser=userService.findElasticUserByUserName(principal.getName());
		User correspondant=userService.findElasticUserByUserName(messageAdapter.correspondantUsername);		
		Chat myChat=chatService.loadSpecificChat(currentUser.getUsername(), correspondant.getUsername());
		Chat correspondantChat=chatService.loadSpecificChat(correspondant.getUsername(), currentUser.getUsername());
		if(myChat==null) {
			myChat=chatService.createChat(currentUser, correspondant);
			correspondantChat=chatService.createChat(correspondant,currentUser);
		}		
		
		Message message=chatService.createChatMessage(currentUser.getUsername(),correspondant.getUsername(), messageAdapter.messageContent);
		myChat.getMessages().add(message);
		correspondantChat.getMessages().add(message);
		myChat.setLastMessageExchanged(message);correspondantChat.setLastMessageExchanged(message);
		correspondantChat.setLastMessageExchanged(message);
		myChat.setLastChatActivity(new Date());correspondantChat.setLastChatActivity(new Date());
		myChat=chatService.saveChat(myChat);correspondantChat=chatService.saveChat(correspondantChat);		
		chatService.notifyCorrespondant(currentUser, message,myChat);
		
		MessageAdapter2 msgAdapt=new MessageAdapter2(message);
		msgAdapt.correspondantFirstName=myChat.getCorrespondantFirstName();
		msgAdapt.completeCorrespondantName=myChat.getCompleteCorrespondantName();
		msgAdapt.correspondantId=myChat.getCorrespondantId();
		msgAdapt.lastChatActivity=myChat.getLastChatActivity();		
		
		return new ResponseEntity(gson.toJson(msgAdapt) ,HttpStatus.OK);
	}
	
	
	private class AcknowledgementWrapper{
		String correspondantUsername;
		String messageId;
	}
	
	@RequestMapping(value="/message_ack", method=RequestMethod.POST)
	public ResponseEntity messageAck(@RequestBody String payLoad,Principal principal) {
		AcknowledgementWrapper ackWrapper=gson.fromJson(payLoad, AcknowledgementWrapper.class);
		Message msg=chatService.acknowledgeMessage(principal.getName(),ackWrapper.correspondantUsername, ackWrapper.messageId);
		chatService.notifyAcknowledgement(ackWrapper.correspondantUsername, ackWrapper.messageId);
		return new ResponseEntity(msg,HttpStatus.OK);
	}
	
	@RequestMapping(value="/keep_alive/{online}")
	public ResponseEntity keepAlive(Principal principal, @PathVariable("online") boolean online) {
		if(principal==null) return null;
		User user=userService.findElasticUserByUserName(principal.getName());
		chatService.sendKeepAliveToMyContacts(user,online);
		return new ResponseEntity("OK",HttpStatus.OK);
	}
	
	@RequestMapping(value="/unread_number")
	public ResponseEntity getUnreadMessageNumber(Principal principal) {
		return new ResponseEntity(chatService.countUnreadMessages(principal.getName()),HttpStatus.OK);
	}

}
