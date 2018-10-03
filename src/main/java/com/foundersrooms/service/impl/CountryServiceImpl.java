package com.foundersrooms.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.location.Country;
import com.foundersrooms.repository.CountryRepository;
import com.foundersrooms.service.CountryService;

@Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	private CountryRepository countryRepository;
	
	@Override
	public Set<Country> getCountryList() {
		// TODO Auto-generated method stub
		return countryRepository.getCountriesList();
	}

	@Override
	public Set<String> getCountriesNameList() {
		// TODO Auto-generated method stub
		return countryRepository.getCountriesNamesInFrench();
	}

}
