package com.foundersrooms.service;

import java.util.List;
import java.util.Set;

import com.foundersrooms.domain.NeedTemp;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.project.Need;



public interface NeedService {

	
	Need save(Need need);
	
	Need findById(Long id);
	
	List<Need> findAll();	
	
	NeedTemp findAllServices();
	
	void removeOne(Long id);
	
	Set<ServiceDetails> getServiceDetailsForAParticularUser(Long userId);
}
