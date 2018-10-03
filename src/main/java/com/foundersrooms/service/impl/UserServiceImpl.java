package com.foundersrooms.service.impl;


import static com.foundersrooms.service.impl.UtilityServiceImpl.toSlug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.UserTemp;
import com.foundersrooms.domain.UserWrapperForNetwork;
import com.foundersrooms.domain.people.Creator;
import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.Investor;
import com.foundersrooms.domain.people.Provider;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.security.Role;
import com.foundersrooms.domain.security.UserRole;
import com.foundersrooms.elasticsearchreprository.ContactRepository;
import com.foundersrooms.elasticsearchreprository.UserElasticRepository;
import com.foundersrooms.repository.NotificationRepository;
import com.foundersrooms.repository.RoleRepository;
import com.foundersrooms.repository.ServiceDetailsRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.UserService;
import com.foundersrooms.service.UtilityService;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private ServiceDetailsRepository serviceDetailsRepository;
	
	@Autowired
	private UserElasticRepository userElasticRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired	
    private ElasticsearchTemplate template;
	
	@Autowired
	private UtilityService utilityService;
	

	@Override
	public User createUserItemByUserType(String userType) {
		switch(userType) {
			case "Creator":
				return new Creator();
			case "Supplier":
				return new Provider();
			default:
				return new Investor();
		}
	}	
	
	public Set<UserRole> getUserRoles(User user,String userType) {
		Set<UserRole> userRoles = new HashSet<>();
		Role role= new Role();;
		switch(userType) {
			case "Creator":				
				role.setRoleID(1);
				role.setName("ROLE_CREATOR");
				userRoles.add(new UserRole(user, role));
				return userRoles;
			case "Supplier":				
				role.setRoleID(3);
				role.setName("ROLE_SUPPLIER");
				userRoles.add(new UserRole(user, role));
				return userRoles;	
			default:
				role.setRoleID(4);
				role.setName("ROLE_INVESTOR");
				userRoles.add(new UserRole(user, role));
				return userRoles;				
		
		}
	}
	
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) {
		User localUser = userRepository.findByUsername(user.getUsername());

		if (localUser != null) {
			LOG.info("User with username {} already exist. Nothing will be done. ", user.getUsername());
		} else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}

			user.getUserRoles().addAll(userRoles);
			
			User elasticUser=new User();
			cloneUser(elasticUser,user);
			
			localUser = userRepository.save(user);
			
			template.putMapping(User.class);
			IndexQuery indexQuery = new IndexQuery();
			indexQuery.setId(localUser.getId().toString());
			indexQuery.setObject(elasticUser);
			template.index(indexQuery);
			template.refresh(User.class);
			userElasticRepository.save(user);
		}

		return localUser;
	}
	
	@Override
	public User createElasticUser(User user) {
		// TODO Auto-generated method stub
		User elasticUser=new User();
		cloneUser(elasticUser,user);	
		elasticUser.setServiceDetails(user.getServiceDetails());
		elasticUser.setExpectations(utilityService.getExpectionsFromServiceDetails(user.getServiceDetails()));		
		template.putMapping(User.class);
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(user.getId().toString());
		indexQuery.setObject(elasticUser);
		template.index(indexQuery);
		template.refresh(User.class);
		userElasticRepository.save(user);		
		return elasticUser;
	}
	

	@Override
	public User save(User user)  {
		user= userRepository.save(user);
		return user;
	}
	
	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);		
	}
	
	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void updateInMemoryUserItem(User user,HashMap<String, Object> mapper) {
		//collect inputs from mapper
		String facebookId = (String) mapper.get("facebookID");
		String phone=(String) mapper.get("phone");
		String bio=(String) mapper.get("bio");
		String codePostal=(String) mapper.get("codePostal");
		String googleID=(String) mapper.get("googleID");
		String intro=(String)mapper.get("intro");
		String linkedInID=(String)mapper.get("linkedInID");
		String country=(String)mapper.get("country");
		String username = (String) mapper.get("username");
		String firstName = (String) mapper.get("firstName");
		String lastName = (String) mapper.get("lastName");
		String town=(String) mapper.get("town");
		String sector=(String) mapper.get("sector");
		String job=(String) mapper.get("job");
		
		//update user fields
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setFacebookID(facebookId);
		user.setPhone(phone);		
		user.setBio(bio);
		user.setCodePostal(codePostal);
		user.setGoogleID(googleID);
		user.setIntro(intro);
		user.setLinkedInID(linkedInID);
		user.setCountry(country);	
		user.setTown(town);
		user.setSector(sector);
		user.setSectorSlug(sector);
		user.setJob(job);
		
	}
	
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	public User findOne(Long id) {
		return userRepository.findOne(id);
	}
	
	public void removeOne(Long id) {
		userRepository.delete(id);
	}
	
	@Override
	public void cloneUser(User user1, User user2) {
		user1.setFirstName(user2.getFirstName());
		user1.setLastName(user2.getLastName());
		user1.setUsername(user2.getUsername());
		user1.setFacebookID(user2.getFacebookID());
		user1.setPhone(user2.getPhone());		
		user1.setBio(user2.getBio());
		user1.setCodePostal(user2.getCodePostal());
		user1.setGoogleID(user2.getGoogleID());
		user1.setIntro(user2.getIntro());
		user1.setLinkedInID(user2.getLinkedInID());
		user1.setCountry(user2.getCountry());	
		user1.setTown(user2.getTown());
		user1.setTownSlug(UtilityServiceImpl.toSlug(user2.getTown()));
		user1.setId(user2.getId());
		user1.setEmail(user2.getEmail());
		user1.setUserType(user2.getUserType());
		user1.setUserTypeSlug(toSlug(user2.getUserType()));
		user1.setSector(user2.getSector());
		user1.setSectorSlug(UtilityServiceImpl.toSlug(user2.getSector()));
		user1.setJob(user2.getJob());
		user1.setInvitationItemsNumber(user2.getInvitationItemsNumber());
		user1.setProfileImageSet(user2.isProfileImageSet());
		user1.setRegisteredByProvider(user2.isRegisteredByProvider());
		user1.setCompleteName(user2.getCompleteName());
	}
	
	public UserTemp clone(User user,Long idRequester) {
		UserTemp userTemp=new UserTemp();
		cloneUser(userTemp, user);
		/*
		userTemp.setFirstName(user.getFirstName());
		userTemp.setLastName(user.getLastName());
		userTemp.setUsername(user.getUsername());
		userTemp.setFacebookID(user.getFacebookID());
		userTemp.setPhone(user.getPhone());		
		userTemp.setBio(user.getBio());
		userTemp.setCodePostal(user.getCodePostal());
		userTemp.setGoogleID(user.getGoogleID());
		userTemp.setIntro(user.getIntro());
		userTemp.setLinkedInID(user.getLinkedInID());
		userTemp.setCountry(user.getCountry());	
		userTemp.setTown(user.getCountry());
		userTemp.setId(user.getId());
		userTemp.setEmail(user.getEmail());*/
		userTemp.setRequesterId(idRequester);
		return userTemp;
		
	}

	@Override
	public void sendNotification(User notifier, User guest, Notification notification) {		
		// TODO Auto-generated method stub
		notifier.getRequetes().add(notification);
		guest.getInvitations().add(notification);		
		userRepository.save(notifier);
		userRepository.save(guest);
	}	

	@Override
	public UserWrapperForNetwork getUserNetwork(Long userId) {
		// TODO Auto-generated method stub
		User user=userRepository.findOne(userId);
		UserWrapperForNetwork result=new UserWrapperForNetwork();
		result.setContacts(user.getContacts());
		result.setUsersInvited(userRepository.findUsersThatISendNotificationTo(userId));
		result.setUserRequests(userRepository.findUsersThatSentMeRequests(userId));
		return result;
	}

	@Override
	public Set<User> findMyContactsByFirstNameUsernameOrLastName(String firstName, String userName, String lastName,Long userId/*,Set<User> projectMembers*/) {
		Set<User> allMatchedContacts=userRepository.findByFirstNameIgnoreCaseContainingOrUsernameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(firstName, userName, lastName);
		return userRepository.findMyContactsByFirstNameUsernameOrLastName(allMatchedContacts,userId/*,projectMembers*/);
	}

	@Override
	public int getNumberOfUsersThatSentMeRequest(Long myId) {
		// TODO Auto-generated method stub
		return userRepository.findUsersThatSentMeRequests(myId).size();
	}

	@Override
	public Set<User> findPeopleThatAreNotYetInMyContactsList(User user) {
		// TODO Auto-generated method stub
		if(user.getContacts().size()!=0)
		return userRepository.findPeopleThatAreNotYetInMyContactsList(user.getId(), user.getContacts());
		else {
			List<User> users=userRepository.findAll();
			users.remove(user);
			return new HashSet<User>(users);
			
		}
			 
	}

	@Override
	public User findElasticUserByUserName(String username) {
		// TODO Auto-generated method stub
		return userElasticRepository.findByUsername(username);
	}

	@Override
	public User elasticSave(User user) {
		// TODO Auto-generated method stub
		return userElasticRepository.save(user);
	}

	
	@Override
	public User saveAndFlush(User user) {
		// TODO Auto-generated method stub
		//return userRepository.saveAndFlush(user);		
		return null;
	}

	@Override
	public ElasticContact buildElasticContact(String ownerUsername,User user) {
		// TODO Auto-generated method stub
		template.putMapping(		ElasticContact.class);
		IndexQuery indexQuery = new IndexQuery();	
		ElasticContact result=new ElasticContact();
		//result.setId(user.getId().toString());
		result.setOwnerUsername(ownerUsername);
		result.setUserId(user.getId());
		result.setFirstName(user.getFirstName());
		result.setLastName(user.getLastName());
		result.setUsername(user.getUsername());
		result.setCompleteName(user.getCompleteName());
		result.setUserType(user.getUserTypeSlug());
		result.setOnline(user.isOnline());
		indexQuery.setObject(result);
		result=contactRepository.save(result);		
		return result;
	}

	@Override
	public boolean isBasicRequirementsProvided(String username) {
		// TODO Auto-generated method stub
		User user=userElasticRepository.findByUsername(username);
		if(user==null) {
			User mysqlUser=userRepository.findByUsername(username);
			user=createElasticUser(mysqlUser);
			/*
			user.setServiceDetails(mysqlUser.getServiceDetails());
			user.setExpectations(utilityService.getExpectionsFromServiceDetails(mysqlUser.getServiceDetails()));
			user=elasticSave(user);*/
		}
		if(user.getTownSlug().equals("") || user.getSectorSlug().equals(""))
			return false;
		return true;
	}

	@Override
	public User findElasticUserById(Long id) {
		// TODO Auto-generated method stub
		return userElasticRepository.findOne(id);
	}

	@Override
	public User findUserByUsernameSlug(String name) {
		// TODO Auto-generated method stub		
		return userRepository.findByUserNameSlug(name);
	}

	@Override
	public User findElasticUserByUsernameSlug(String name) {
		// TODO Auto-generated method stub
		return userElasticRepository.findByUserNameSlug(name);
	}

	@Override
	public Iterable<User> findAllElasticUsers() {
		// TODO Auto-generated method stub
		return userElasticRepository.findAll() ;

	}



}
