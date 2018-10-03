package com.foundersrooms.service;

import java.util.Set;

import com.foundersrooms.domain.location.Country;

public interface CountryService {
	public Set<Country> getCountryList();
	public Set<String> getCountriesNameList();
}
