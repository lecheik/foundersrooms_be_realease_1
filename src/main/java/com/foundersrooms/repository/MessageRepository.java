package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.messenger.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

	@Query("SELECT msg FROM Message msg WHERE (msg.ownerUsername=:ownerUsername AND msg.correspondantUsername=:correspondantUsername) OR (msg.ownerUsername=:correspondantUsername AND msg.correspondantUsername=:ownerUsername) ORDER BY id ASC")
	Set<Message> loadChatMessages(@Param("ownerUsername") String ownerUsername, @Param("correspondantUsername") String correspondantUsername);
}
