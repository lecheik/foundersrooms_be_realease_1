package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.foundersrooms.domain.location.Country;


public interface CountryRepository extends CrudRepository<Country, Long> {

	@Query("SELECT c FROM Country c")
	public Set<Country> getCountriesList();
	
	@Query("SELECT c.nameInFrench FROM Country c")
	public Set<String> getCountriesNamesInFrench();
	
	
}
