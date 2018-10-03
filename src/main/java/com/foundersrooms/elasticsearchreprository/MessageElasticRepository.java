package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.messenger.Message;

public interface MessageElasticRepository extends ElasticsearchRepository<Message, Long> {
	long countByCorrespondantUsernameAndAcknowledged(String username,boolean messageAck);
	
	//Set<Message> findByMessageRefContainingAllIgnoreCaseOrderBySendTimeAsc(String messageRef);
	Set<Message> findByOwnerUsernameAndCorrespondantUsernameOrOwnerUsernameAndCorrespondantUsernameOrderBySendTimeAsc(String username1,String username2,String username3,String username4);
	//int countByCorrespondantUsernameAndOwnerUsernameOrOwnerUsernameAndCorrespondantUsername(String username1,String username2,String username3,String username4);
}
