package com.foundersrooms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.domain.NeedTemp;
import com.foundersrooms.service.NeedService;
import com.foundersrooms.service.UtilityService;

@RestController
@RequestMapping("/needs")
public class NeedResource {

	@Autowired
	private NeedService needService;
	
	@Autowired
	private UtilityService utilityService;
	
	@RequestMapping(value="/")
	public NeedTemp getInvestmentAndFinanceList(){
		return needService.findAllServices();
	}
	
	@RequestMapping(value="/{user_id}")
	public ResponseEntity getServicesDetailsForAParticularUser(@PathVariable("user_id") Long userId){			
		String result=utilityService.getAllUserServicesDetails(userId)	;	
		return new ResponseEntity(result,HttpStatus.OK);
	}	
}
