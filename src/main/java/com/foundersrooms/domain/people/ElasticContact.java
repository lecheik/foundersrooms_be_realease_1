package com.foundersrooms.domain.people;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "foundersrooms", type = "contacts")
public class ElasticContact implements Serializable {
	/**
	 * 
	 */	
	
	private static final long serialVersionUID = -8996250000452094511L;
	@Id
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String id;	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String ownerUsername="";
	@Field(type = FieldType.Long, index = FieldIndex.not_analyzed, store = true)
	private Long userId;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String username;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String firstName;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String completeName;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String lastName;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String userType;
	@Field(type = FieldType.Boolean, index = FieldIndex.not_analyzed, store = true)
	private boolean online;	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public String getCompleteName() {
		return completeName;
	}
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	
	public String getOwnerUsername() {
		return ownerUsername;
	}
	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		ElasticContact other = (ElasticContact) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;*/
		ElasticContact other = (ElasticContact) obj;
		return username.equals(other.getUsername());		
	}	
	
}
