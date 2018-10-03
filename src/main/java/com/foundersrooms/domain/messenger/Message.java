package com.foundersrooms.domain.messenger;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Document(indexName = "foundersrooms", type = "messages")
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8854875800785249536L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private Long id;
	
	@Field(type = FieldType.Long, index = FieldIndex.not_analyzed, store = true)
	private Long orderField;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String ownerUsername;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String correspondantUsername;	
	
	@Lob
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String content;	
	
	@Field(type = FieldType.Date,index = FieldIndex.not_analyzed, store = true)
	private Date sendTime;
	
	@Field(type = FieldType.Date)
	private Date receivedDate;
	
	@Field(type = FieldType.Boolean)
	private boolean acknowledged=false;
	/*
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="chat_id")
	@JsonIgnore
	private Chat chat;*/

	public String getContent() {
		return content;
	}

	public Long getOrderField() {
		return orderField;
	}

	public void setOrderField(Long orderField) {
		this.orderField = orderField;
	}

	public String getCorrespondantUsername() {
		return correspondantUsername;
	}

	public void setCorrespondantUsername(String correspondantUsername) {
		this.correspondantUsername = correspondantUsername;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", content=" + content + ", sendTime=" + sendTime + ", receivedDate="
				+ receivedDate + ", acknowledged=" + acknowledged + "]";
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
		/*if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;*/
		Message other=(Message)obj;
		return id.equals(other.id);
	}
	
	

}
