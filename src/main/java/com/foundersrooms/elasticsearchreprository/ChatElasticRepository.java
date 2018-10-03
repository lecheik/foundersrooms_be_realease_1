package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.messenger.Chat;

public interface ChatElasticRepository extends ElasticsearchRepository<Chat, Long> {

	Set<Chat> findByOwnerUsernameAndCorrespondantUsername(String ownerUsername,String correspondantUsername);	
}
