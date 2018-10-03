package com.foundersrooms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.location.City;
import com.foundersrooms.domain.people.User;



public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
	User findByUsername(String username);

	User findByEmail(String email);

	@Query("SELECT u FROM User u ORDER BY u.firstName ASC, u.lastName ASC ")
	List<User> findAll();
	
	@Query("SELECT u2 FROM User u1, User u2, Notification n WHERE n.notifier.id=u1.id AND n.guest.id=u2.id AND u1.id=:myId AND n.isPending=true")
	Set<User> findUsersThatISendNotificationTo(@Param("myId") Long myId);
	
	@Query("SELECT u2 FROM User u1, User u2, Notification n WHERE n.guest.id=u1.id AND n.notifier.id=u2.id AND u1.id=:myId AND n.isPending=true")
	Set<User> findUsersThatSentMeRequests(@Param("myId") Long myId);
	
	Set<User> findByFirstNameIgnoreCaseContainingOrUsernameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String firstName,String username,String lastName);

	@Query("SELECT DISTINCT c  FROM User u INNER JOIN u.contacts c WHERE c IN :matchedDataSet AND u.id=:myId")
	Set<User> findMyContactsByFirstNameUsernameOrLastName(@Param("matchedDataSet") Set<User> matchedDataSet,@Param("myId") Long myId/*,@Param("projectMembers") Set<User> projectMembers*/);
	
	@Query("SELECT u FROM User u WHERE u.id<>:myId AND u NOT IN :myContacts ORDER BY u.firstName ASC, u.lastName ASC")
	Set<User> findPeopleThatAreNotYetInMyContactsList(@Param("myId") Long myId,@Param("myContacts") Set<User> myContacts);
	
	List<User> findByCompleteNameContainingAllIgnoreCaseOrderByLastNameAsc(String namePart);
	
	Set<User> findByContacts_LastNameContainingAllIgnoreCaseOrderByLastNameAsc(String namePart);
	
	User findByUserNameSlug(String param);

}
