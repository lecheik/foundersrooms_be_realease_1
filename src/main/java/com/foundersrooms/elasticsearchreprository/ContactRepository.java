package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.people.ElasticContact;

public interface ContactRepository extends ElasticsearchRepository<ElasticContact, String> {	
	ElasticContact findByOwnerUsernameAndUsername(String ownerUsername,String contactUsername);
	
	Set<ElasticContact> findByOwnerUsernameOrUsername(String ownerUsername,String contactUsername);
	
	Set<ElasticContact> findByOwnerUsernameAndCompleteNameContainingAllIgnoreCase(String ownerUsername,String payLoad);
	
	Set<ElasticContact> findByUsername(String username);
}
