package com.foundersrooms.domain;

import com.foundersrooms.domain.people.User;

public class UserTemp extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isAContact=false;	
	private Long requesterId; 
	private boolean isConnectionNotificationReceived=false;
	
	
	public UserTemp() {
		super();
		
	}

	public boolean isAContact() {
		return isAContact;
	}
	public void setAContact(boolean isAContact) {
		this.isAContact = isAContact;
	}
	public Long getRequesterId() {
		return requesterId;
	}
	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}
	public boolean isConnectionNotificationReceived() {
		return isConnectionNotificationReceived;
	}
	public void setConnectionNotificationReceived(boolean isConnectionNotificationReceived) {
		this.isConnectionNotificationReceived = isConnectionNotificationReceived;
	}
	
	

}
