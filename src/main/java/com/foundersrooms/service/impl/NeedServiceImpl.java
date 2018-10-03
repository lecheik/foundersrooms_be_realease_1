package com.foundersrooms.service.impl;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.NeedTemp;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.project.Need;
import com.foundersrooms.repository.NeedRepository;
import com.foundersrooms.repository.ServiceDetailsRepository;
import com.foundersrooms.service.NeedService;

@Service
public class NeedServiceImpl implements NeedService {

	@Autowired
	private NeedRepository needRepository;
	
	@Autowired
	private ServiceDetailsRepository serviceDetailsRepository;
	
	@Override
	public Need save(Need need) {
		// TODO Auto-generated method stub
		return needRepository.save(need);
	}

	@Override
	public Need findById(Long id) {
		// TODO Auto-generated method stub
		return needRepository.findOne(id);
	}

	@Override
	public List<Need> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeOne(Long id) {
		// TODO Auto-generated method stub
		needRepository.delete(id);
	}
	
	public NeedTemp findAllServices(){
		NeedTemp needTemp=new NeedTemp();
		needTemp.setInvestment(needRepository.findAllInvestmentServices());
		needTemp.setAdvice(needRepository.findAllAdviceServices());
		needTemp.setCoaching(needRepository.findAllCoachingServices());
		return needTemp;
	}

	@Override
	public Set<ServiceDetails> getServiceDetailsForAParticularUser(Long userId) {
		// TODO Auto-generated method stub
		Set<ServiceDetails> result=serviceDetailsRepository.getUserServicesDetails(userId);
		if(result.size()==0) {			
			return null;
		}			
		return serviceDetailsRepository.getUserServicesDetails(userId);
	}

}
