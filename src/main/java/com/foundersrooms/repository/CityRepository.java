package com.foundersrooms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.foundersrooms.domain.location.City;

public interface CityRepository extends CrudRepository<City, Long> {

	
	public Set<City> findByNameStartsWithAllIgnoreCaseOrderByNameAsc(String namePart);
	
	public long countByName(String name);
	
	public List<City> findByNameAllIgnoreCase(String name);
}
