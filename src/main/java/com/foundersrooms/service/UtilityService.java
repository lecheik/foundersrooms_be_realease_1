package com.foundersrooms.service;


import java.util.List;
import java.util.Set;

import com.foundersrooms.domain.location.City;
import com.foundersrooms.domain.people.Expectation;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.people.User;

public interface UtilityService {
	Set<City> findAllCities();
	Set<City> findCitiesByCityName(String cityName);
	boolean isCityExists(String value);
	Object findCityByName(String value);
	Set<ServiceDetails> saveServiceDetails(String details,User user);
	Set<ServiceDetails> mergeElasticSearchServiceDetails(Set<ServiceDetails> serviceDetails, User user);
	String getAllUserServicesDetails(Long userId);
	String getUserServicesDetailsByServiceType(Long userId,String serviceType);	
	void removeUnusedServiceDetailsSmartly(Set<ServiceDetails> currentServicesDetails,Long userId,String serviceType);
	void removeUnusedElasticServiceDetailsSmartly(String services,Long userId,String serviceType);
	void deleteServicesDetailsByServiceType(Long userId,String serviceType);	
	Set<ServiceDetails> getServiceDetails(String services, User user);
	List<Expectation> getExpectionsFromServiceDetails(Set<ServiceDetails> services);
	List<String> loadSectors();
	List<List<String>> loadJobs();
	List<List<String>> loadServices();
}
