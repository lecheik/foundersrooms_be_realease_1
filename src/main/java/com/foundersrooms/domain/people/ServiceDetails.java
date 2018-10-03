package com.foundersrooms.domain.people;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
//@Document(indexName = "foundersrooms"/*, type = "services"*/)
public class ServiceDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	//@Field(type = FieldType.Object,  store = true)
	@Embedded
	private ServiceField serviceField;	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String serviceName;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String serviceNameSlug;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String serviceType="";
	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int minAmount;
	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int maxAmount;
	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int rate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	@Transient
	private boolean isDirty=false;
	
	public ServiceDetails() {
		super();
		serviceField=new ServiceField();
		// TODO Auto-generated constructor stub
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ServiceField getServiceField() {
		return serviceField;
	}
	public void setServiceField(ServiceField serviceField) {
		this.serviceField = serviceField;
	}

	public int getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}
	public int getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public String getServiceNameSlug() {
		return serviceNameSlug;
	}
	public void setServiceNameSlug(String serviceNameSlug) {
		this.serviceNameSlug = serviceNameSlug;
	}
	
	@Override
	public String toString() {
		return "ServiceDetails [serviceNameSlug=" + serviceNameSlug + ", serviceType=" + serviceType + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result + ((serviceType == null) ? 0 : serviceType.hashCode());
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
		ServiceDetails other = (ServiceDetails) obj;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		if (serviceType == null) {
			if (other.serviceType != null)
				return false;
		} else if (!serviceType.equals(other.serviceType))
			return false;
		return true;
	}
	
	
	


}
