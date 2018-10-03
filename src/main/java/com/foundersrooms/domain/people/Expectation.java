package com.foundersrooms.domain.people;


public class Expectation {
	private Long id;		
	private String description;
	private String serviceName;	
	private String serviceNameSlug;	
	private String serviceType="";	
	private int minAmount;	
	private int maxAmount;	
	private int minDuration;
	private int maxDuration;
	private int rate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMinDuration() {
		return minDuration;
	}
	public void setMinDuration(int minDuration) {
		this.minDuration = minDuration;
	}
	public int getMaxDuration() {
		return maxDuration;
	}
	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceNameSlug() {
		return serviceNameSlug;
	}
	public void setServiceNameSlug(String serviceNameSlug) {
		this.serviceNameSlug = serviceNameSlug;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serviceNameSlug == null) ? 0 : serviceNameSlug.hashCode());
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
		Expectation other = (Expectation) obj;
		if (serviceNameSlug == null) {
			if (other.serviceNameSlug != null)
				return false;
		} else if (!serviceNameSlug.equals(other.serviceNameSlug))
			return false;
		return true;*/
		Expectation other = (Expectation) obj;
		return serviceNameSlug.equals(other.serviceNameSlug);
	}
	@Override
	public String toString() {
		return "Expectation [serviceNameSlug=" + serviceNameSlug + ", minAmount=" + minAmount + ", maxAmount="
				+ maxAmount + ", minDuration=" + minDuration + ", maxDuration=" + maxDuration + ", rate=" + rate + "]";
	}
	
}
