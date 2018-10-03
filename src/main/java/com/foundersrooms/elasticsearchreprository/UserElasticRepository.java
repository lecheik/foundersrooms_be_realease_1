package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.people.User;

public interface UserElasticRepository extends ElasticsearchRepository<User, Long> {
	User findByUsername(String username);
	
	User findByUserNameSlug(String param);
	/*
	Set<User> findByElasticContacts_lastNameContainingAllIgnoreCaseOrderByLastNameAsc(String namePart);
	Set<User> findByElasticContacts_completeNameContainingAllIgnoreCaseOrderByLastNameAsc(String namePart);
	*/
}
