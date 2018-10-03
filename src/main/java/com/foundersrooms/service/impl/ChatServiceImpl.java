package com.foundersrooms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foundersrooms.domain.messenger.Chat;
import com.foundersrooms.domain.messenger.Message;
import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.ChatElasticRepository;
import com.foundersrooms.elasticsearchreprository.MessageElasticRepository;
import com.foundersrooms.repository.MessageRepository;
import com.foundersrooms.service.ChatService;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.UserService;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

@Service
public class ChatServiceImpl implements ChatService {
	
	@Autowired
	private ChatElasticRepository chatElasticRepository;
	
	@Autowired
	private MessageElasticRepository messageElasticRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
    private ElasticsearchTemplate elasticsearchTemplate;	
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	Gson gson=new Gson();
	
	Faker faker=new Faker();
	
	class MessageAdapter extends Message{
		MessageAdapter(Message message){
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
		User correspondant;
	}
	
	@Override
	public Chat saveChat(Chat chat) {
		return chatElasticRepository.save(chat);
	}
	
	@Override
	public Set<Chat> loadWholeExchanges(User user) {
		// TODO Auto-generated method stub
		BoolQueryBuilder queryBuilder=new BoolQueryBuilder();
		queryBuilder
		.must(matchQuery("ownerUsername", user.getUsername()));
		
		SearchQuery build = new NativeSearchQueryBuilder()               
				.withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("lastChatActivity").order(SortOrder.ASC))
                .build();
		List<Chat> result = elasticsearchTemplate.queryForList(build, Chat.class);
		return new HashSet<Chat>(result);
	}

	@Override
	public Set<Message> loadChatMessages(String userLoggedInUsername, String correspondantUsername) {		
		// TODO Auto-generated method stub
		
		BoolQueryBuilder queryBuilder1=new BoolQueryBuilder();
		BoolQueryBuilder queryBuilder2=new BoolQueryBuilder();
		queryBuilder1.must(matchQuery("ownerUsername", userLoggedInUsername)).must(matchQuery("correspondantUsername", correspondantUsername));
		queryBuilder2.must(matchQuery("ownerUsername", correspondantUsername)).must(matchQuery("correspondantUsername", userLoggedInUsername));
		int pageSize=(int) messageElasticRepository.count();
		if(pageSize==0) pageSize=10;
		SearchQuery build = new NativeSearchQueryBuilder()               
				.withQuery(
						boolQuery().
						should(queryBuilder1)
						.should(queryBuilder2)						
							//must(matchQuery("ownerUsername", userLoggedInUsername)).must(matchQuery("correspondantUsername", correspondantUsername))
						)               
				//.withSort(SortBuilders.fieldSort("orderField").order(SortOrder.ASC))
				.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC))
				.withPageable(new PageRequest(0,pageSize))
                .build();		
		List<Message> result = elasticsearchTemplate.queryForList(build, Message.class);
		return new HashSet<Message>(result);		
		/*
		if(result.size()==0)
			return new HashSet<Message>();
		return new HashSet<Message>(result.get(0).getMessages());*/				
	}

	@Override
	public Chat loadSpecificChat(String currentLoggedInUsername, String correspondantUsername) {
		// TODO Auto-generated method stub
		Set<Chat> result=chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(currentLoggedInUsername, correspondantUsername);
		if(result.size()==0) return null;
		return (new ArrayList<Chat>(result)).get(0);
	}

	@Override
	public Chat createChat(User owner, User correspondant) {
		//elasticsearchTemplate.putMapping(Chat.class);
		//IndexQuery indexQuery = new IndexQuery();	
		Chat myChat=new Chat();
		String id=faker.idNumber().valid();
		//id=faker.number().randomNumber(10, true);
		//myChat.setId(Long.parseLong(id.replaceAll("-", "")));
		myChat.setId(faker.number().randomNumber(10, true));
		//myChat.setId(chatElasticRepository.count()+1);
		myChat.setCorrespondantId(correspondant.getId());
		myChat.setCorrespondantFirstName(correspondant.getFirstName());
		myChat.setOwnerUsername(owner.getUsername());
		myChat.setCorrespondantUsername(correspondant.getUsername());
		myChat.setCompleteCorrespondantName(correspondant.getFirstName()+" "+correspondant.getLastName());
		//indexQuery.setObject(myChat);
		myChat=chatElasticRepository.save(myChat);		
		return myChat;				
	}

	@Override
	public Message createChatMessage(String ownerUsername,String correspondantUsername, String messageContent) {		
		//elasticsearchTemplate.putMapping(Message.class);
		//IndexQuery indexQuery = new IndexQuery();
		Message message=new Message();
		//message.setId(String.valueOf(faker.number().randomNumber(10, true)));
		//message.setId(String.valueOf(messageElasticRepository.count()+1));
		//message.setId(messageElasticRepository.count()+1);
		message.setOrderField(messageElasticRepository.count()+1);
		message.setContent(messageContent);
		message.setOwnerUsername(ownerUsername);
		message.setCorrespondantUsername(correspondantUsername);
		message.setSendTime(new Date());
		message=messageRepository.save(message);
		//indexQuery.setObject(message);
		message=messageElasticRepository.save(message);
		
		
		return message;
	}
	

	@Override
	public void notifyCorrespondant(User correspondant, Message message, Chat chat) {
		// TODO Auto-generated method stub
		MessageAdapter msgAdapt=new MessageAdapter(message);
		msgAdapt.correspondantFirstName=correspondant.getFirstName();
		msgAdapt.completeCorrespondantName=correspondant.getFirstName()+" "+correspondant.getLastName();
		msgAdapt.correspondantId=correspondant.getId();
		//msgAdapt.lastChatActivity=chat.getLastChatActivity();
		msgAdapt.lastChatActivity=message.getSendTime();
		msgAdapt.correspondant=correspondant;		
		messagingTemplate.convertAndSend("/topic/chat/"+chat.getCorrespondantUsername(), gson.toJson(msgAdapt));
	}

	
	@Override
	public void notifyAcknowledgement(String correspondantUsername, String messageId) {
		// TODO Auto-generated method stub
		messagingTemplate.convertAndSend("/topic/chat/ack/"+correspondantUsername,messageId);
	}

	@Override
	public Message acknowledgeMessage(String ownerUsername,String correspondantUsername,String messageId) {
		Message message=messageElasticRepository.findOne(Long.valueOf(messageId));
		message.setAcknowledged(true);
		message.setReceivedDate(new Date());
		messageElasticRepository.save(message);
		//update related items on message collection of respective chats
		Set<Chat> result=chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(ownerUsername, correspondantUsername);
		if(result.size()==0) return null;
		Chat ownerChat=new ArrayList<Chat>(chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(ownerUsername, correspondantUsername)).get(0);
		Chat correspondantChat=new ArrayList<Chat>(chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(correspondantUsername, ownerUsername)).get(0);
	    if(message.getId().equals(ownerChat.getLastMessageExchanged().getId()))
	    	ownerChat.getLastMessageExchanged().setReceivedDate(message.getReceivedDate());
	    if(message.getId().equals(correspondantChat.getLastMessageExchanged().getId()))
	    	correspondantChat.getLastMessageExchanged().setReceivedDate(message.getReceivedDate());
		Set<Message> ownerMessages=ownerChat.getMessages();
		Set<Message> correspondantMessages=correspondantChat.getMessages();
	    for (Iterator<Message> it = ownerMessages.iterator(); it.hasNext();) {
	        Message msg = it.next();
	        if (msg.equals(message)) {
	            msg.setAcknowledged(true);
	            msg.setReceivedDate(message.getReceivedDate());
	            chatElasticRepository.save(ownerChat);
	            break;
	        }
	    }
	    for (Iterator<Message> it = correspondantMessages.iterator(); it.hasNext();) {
	        Message msg = it.next();
	        if (msg.equals(message)) {
	            msg.setAcknowledged(true);
	            msg.setReceivedDate(message.getReceivedDate());
	            chatElasticRepository.save(correspondantChat);
	            break;
	        }
	    }	    
		return message;
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
	public void sendKeepAliveToMyContacts(User user, boolean status) {
		// TODO Auto-generated method stub
		contactService.updateOnlineStatus(user.getUsername(), status);		
		Set<ElasticContact> contacts=user.getElasticContacts();		
		for(ElasticContact contact:contacts) {
			String endPoint="/topic/keep_alive/"+contact.getUsername();			
			messagingTemplate.convertAndSend(endPoint,gson.toJson(new KeepAliveMessage(user.getUsername(),status)) );
		}
		
	}

	@Override
	public boolean isChatExists(String userLoggedInUsername, String correspondantUsername) {
		// TODO Auto-generated method stub
		Set<Chat> result=chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(userLoggedInUsername, correspondantUsername);
		if(result.size()==0) return false;
		return true;
	}
	
	private Message ackMessage(Long messageId, Date ackDate ) {
		Message correspondingMessageDocument=messageElasticRepository.findOne(messageId);
		correspondingMessageDocument.setAcknowledged(true);
		correspondingMessageDocument.setReceivedDate(ackDate);
		return messageElasticRepository.save(correspondingMessageDocument);
	}
	
	
	@Override
	public void ackAllMessagesExchangedWithACorrespondant(String userLoggedInUsername, String correspondantUsername) {
		// TODO Auto-generated method stub
		Date currentDate=new Date();
		Set<Chat> chatArray=chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(userLoggedInUsername, correspondantUsername);
		if(chatArray.size()==0) return;
		Chat ownerChat=new ArrayList<Chat>(chatArray).get(0);
		Chat correspondantChat=new ArrayList<Chat>(chatElasticRepository.findByOwnerUsernameAndCorrespondantUsername(correspondantUsername, userLoggedInUsername)).get(0);
		Set<Message> ownerMessages=ownerChat.getMessages();
		Set<Message> correspondantMessages=correspondantChat.getMessages();
	    for (Iterator<Message> it = ownerMessages.iterator(); it.hasNext();) {
	        Message msg = it.next();
	        if (!msg.isAcknowledged()) {
	            msg.setAcknowledged(true);
	            msg.setReceivedDate(currentDate);
	            ackMessage(msg.getId(),currentDate);	            
	            chatElasticRepository.save(ownerChat);	           
	        }
	    }
	    for (Iterator<Message> it = correspondantMessages.iterator(); it.hasNext();) {
	        Message msg = it.next();
	        if (!msg.isAcknowledged()) {
	            msg.setAcknowledged(true);
	            msg.setReceivedDate(currentDate);
	            chatElasticRepository.save(correspondantChat);	       
	        }
	    }		
	}

	@Override
	public long countUnreadMessages(String username) {
		// TODO Auto-generated method stub
		return messageElasticRepository.countByCorrespondantUsernameAndAcknowledged(username, false);
	}

	@Transactional
	@Override
	public void clearAllChatsAndMessages() {
		// TODO Auto-generated method stub
		messageElasticRepository.deleteAll();
		chatElasticRepository.deleteAll();
	}

	@Override
	public Set<Message> loadMySQLChatMessages(String userLoggedInUsername, String correspondantUsername) {
		// TODO Auto-generated method stub
		return messageRepository.loadChatMessages(userLoggedInUsername, correspondantUsername);
	}

}
