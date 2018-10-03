package com.foundersrooms.domain;

import com.foundersrooms.domain.people.User;

public class NotificationWebSocketToken {
	private int codeOrder;//0:remove ; 1:add
	private Long idemId;
	private int targetArray;
	private User user;
	public NotificationWebSocketToken(int codeOrder, Long idemId, int targetArray) {
		super();
		this.codeOrder = codeOrder;
		this.idemId = idemId;
		this.targetArray = targetArray;
	}
	
	public NotificationWebSocketToken() {
		super();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getCodeOrder() {
		return codeOrder;
	}
	public void setCodeOrder(int codeOrder) {
		this.codeOrder = codeOrder;
	}
	public Long getIdemId() {
		return idemId;
	}
	public void setIdemId(Long idemId) {
		this.idemId = idemId;
	}
	public int getTargetArray() {
		return targetArray;
	}
	public void setTargetArray(int targetArray) {
		this.targetArray = targetArray;
	}
	
}
