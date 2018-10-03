package com.foundersrooms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.UserTemp;
import com.foundersrooms.domain.UserWrapperForNetwork;
import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.security.UserRole;



public interface UserService {
	
	User createUser(User user, Set<UserRole> userRoles);
	
	User createElasticUser(User user);
	
	User findByUsername(String username);
	
	User findByEmail (String email);
	
	User save(User user);
	
	User saveAndFlush(User user);
	
	User elasticSave(User user);
	
	User findById(Long id);
	
	List<User> findAll();
	
	User findOne(Long id);
	
	User findElasticUserById(Long id);
	
	Iterable<User> findAllElasticUsers();
	
	void removeOne(Long id);
	
	void updateInMemoryUserItem(User user,HashMap<String, Object> mapper);
	
	UserTemp clone(User user, Long requesterId);
	
	void sendNotification(User notifier, User guest, Notification notification);	
	
	UserWrapperForNetwork getUserNetwork(Long userId);
	
	Set<User> findMyContactsByFirstNameUsernameOrLastName(String firstName,String userName,String lastName,Long userId/*,Set<User> projectMembers*/);
	
	int getNumberOfUsersThatSentMeRequest(Long myId);	
	
	Set<User> findPeopleThatAreNotYetInMyContactsList(User user);
	
	Set<UserRole> getUserRoles(User user,String userType);
	
	User createUserItemByUserType(String userType);
	
	User findElasticUserByUserName(String username);
	
	User findElasticUserByUsernameSlug(String name);
	
	void cloneUser(User user1, User user2);
	
	ElasticContact buildElasticContact(String ownerUsername,User user);
	
	boolean isBasicRequirementsProvided(String username);
	
	User findUserByUsernameSlug(String param);
}
