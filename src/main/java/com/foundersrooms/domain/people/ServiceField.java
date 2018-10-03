package com.foundersrooms.domain.people;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Embeddable
//@Document(indexName = "foundersrooms"/*, type = "service_fields"*/)
public class ServiceField implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String description;
	//@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int minDuration;
	//@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private int maxDuration;

	public ServiceField() {
		super();
		// TODO Auto-generated constructor stub
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + maxDuration;
		result = prime * result + minDuration;
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
		ServiceField other = (ServiceField) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (maxDuration != other.maxDuration)
			return false;
		if (minDuration != other.minDuration)
			return false;
		return true;
	}
	
	
}
