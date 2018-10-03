package com.foundersrooms.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.domain.location.City;
import com.foundersrooms.domain.location.Country;
import com.foundersrooms.service.CountryService;
import com.foundersrooms.service.UtilityService;

@RestController
@RequestMapping("/utility")
public class UtilityResource {

	@Autowired
	CountryService countryService;
	
	@Autowired
	UtilityService utilityService;
	
	@RequestMapping("/get_countries")
	public Set<Country> getCountryList(){
		return countryService.getCountryList();
	}
	
	@RequestMapping("/get_all_cities")
	public Set<City> getAllCities(){
		return utilityService.findAllCities();
	}
	
	@RequestMapping("/get_all_cities_name")
	public Set<String> getAllCitiesNames(){
		return countryService.getCountriesNameList();
	}
	
	@RequestMapping("/get_city_name_containing/{value}")
	public Set<City> getCitiesWithCityNameContaining(@PathVariable("value") String value){
		return utilityService.findCitiesByCityName(value);
	}
	
	@RequestMapping("/is_city_name_exists/{value}")
	public boolean isCityNameExists(@PathVariable("value") String value) {
		return utilityService.isCityExists(value.trim());				
	}
	
	@RequestMapping("/get_city_infos/{value}")
	public Object getCityInfos(@PathVariable("value") String value) {
		return utilityService.findCityByName(value.trim());
	}
	
}
