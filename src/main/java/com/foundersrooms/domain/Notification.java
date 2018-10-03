package com.foundersrooms.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.foundersrooms.domain.people.User;


@Entity
public class Notification implements Serializable{
	
	private static final long serialVersionUID = 902783495L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;
	
	private Date timeOfOccurence;
	
	private Date timeOfAck;
	
	@Column(nullable=true)
	private boolean isPending;
	
	@ManyToOne(fetch = FetchType.EAGER,optional=false)
	@JoinColumn(name="notifier_id")
	private User notifier;
	
	@ManyToOne(fetch = FetchType.EAGER,optional=false)
	@JoinColumn(name="guest_id")
	private User guest;
	
	
	public Notification() {
		this.isPending=true;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Date getTimeOfOccurence() {
		return timeOfOccurence;
	}


	public void setTimeOfOccurence(Date timeOfOccurence) {
		this.timeOfOccurence = timeOfOccurence;
	}


	public Date getTimeOfAck() {
		return timeOfAck;
	}


	public void setTimeOfAck(Date timeOfAck) {
		this.timeOfAck = timeOfAck;
	}



	public boolean isPending() {
		return isPending;
	}


	public void setPending(boolean isPending) {
		this.isPending = isPending;
	}


	public User getNotifier() {
		return notifier;
	}


	public void setNotifier(User notifier) {
		this.notifier = notifier;
	}


	public User getGuest() {
		return guest;
	}


	public void setGuest(User guest) {
		this.guest = guest;
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
		Notification other = (Notification) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
