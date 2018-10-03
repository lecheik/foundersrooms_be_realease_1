package com.foundersrooms.domain.messenger;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

//@Entity
@Document(indexName = "foundersrooms", type = "chats")
public class Chat implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2028530068130158118L;

	@Id
	@Field(type = FieldType.Long, index = FieldIndex.not_analyzed, store = true)
	private Long id;	
		
	/*@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner_id")
	@Field(type=FieldType.Nested)
	private User owner;*/
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String ownerUsername="";
	/*
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="correspondant_id")
	@Field(type = FieldType.Nested)
	private User correspondant;*/
	@Field(type=FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String correspondantFirstName="";
	@Field(type=FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String correspondantUsername="";
	@Field(type=FieldType.Long, index = FieldIndex.not_analyzed, store = true)
	private Long correspondantId;
	
	@Field(type=FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String completeCorrespondantName;
	
	/*@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="chat")
	@OrderBy("id ASC")*/
	@Field(type = FieldType.Nested)
	private Set<Message> messages=new HashSet();
	
	/*@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
	@JoinColumn(name="last_message_id")*/
	@Field(type = FieldType.Nested)
	private Message lastMessageExchanged;
	
	@Field(type = FieldType.Date,index = FieldIndex.not_analyzed, store = true)
	private Date lastChatActivity;

	public Long getCorrespondantId() {
		return correspondantId;
	}

	public String getCorrespondantFirstName() {
		return correspondantFirstName;
	}

	public void setCorrespondantFirstName(String correspondantFirstName) {
		this.correspondantFirstName = correspondantFirstName;
	}

	public void setCorrespondantId(Long correspondantId) {
		this.correspondantId = correspondantId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public String getCorrespondantUsername() {
		return correspondantUsername;
	}

	public Date getLastChatActivity() {
		return lastChatActivity;
	}

	public void setLastChatActivity(Date lastChatActivity) {
		this.lastChatActivity = lastChatActivity;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Message getLastMessageExchanged() {
		return lastMessageExchanged;
	}

	public void setLastMessageExchanged(Message lastMessageExchanged) {
		this.lastMessageExchanged = lastMessageExchanged;
	}

	public String getCompleteCorrespondantName() {
		return completeCorrespondantName;
	}

	public void setCompleteCorrespondantName(String completeCorrespondantName) {
		this.completeCorrespondantName = completeCorrespondantName;
	}

	public void setCorrespondantUsername(String correspondantUsername) {
		this.correspondantUsername = correspondantUsername;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chat other = (Chat) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Chat [id=" + id + ", ownerUsername=" + ownerUsername + ", correspondantUsername="
				+ correspondantUsername + "]";
	}

	
}
