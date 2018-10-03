package com.foundersrooms.controller;

import static com.foundersrooms.service.impl.UtilityServiceImpl.toSlug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.foundersrooms.config.SecurityConfig;
import com.foundersrooms.config.SecurityUtility;
import com.foundersrooms.domain.UserTemp;
import com.foundersrooms.domain.UserWrapperForNetwork;
import com.foundersrooms.domain.people.Creator;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.security.Role;
import com.foundersrooms.domain.security.UserRole;
import com.foundersrooms.service.AmazonClient;
import com.foundersrooms.service.ContactService;
import com.foundersrooms.service.NotificationService;
import com.foundersrooms.service.ProjectService;
import com.foundersrooms.service.UserService;
import com.foundersrooms.service.UtilityService;
import com.foundersrooms.utility.MailConstructor;
import com.github.javafaker.Faker;

@RestController
@RequestMapping("/user")
public class UserResource {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ContactService contactService;

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private UtilityService utilityService;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private AmazonClient amazonS3Client;	
	
	Faker fake=new Faker();


	@RequestMapping(value = "/new_non_social", method = RequestMethod.POST)
	public ResponseEntity newNonSocialUser(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		String firstName=mapper.get("firstName");
		String lastName=mapper.get("lastName");
		String username = mapper.get("username");
		String userEmail = mapper.get("email");		
		String userType=mapper.get("userType");	
		//String password=mapper.get("password");	
		
		if (userService.findByUsername(username) != null) {
			return new ResponseEntity("usernameExists", HttpStatus.BAD_REQUEST);
		}
		
		if (userService.findByEmail(userEmail) != null) {
			return new ResponseEntity("emailExists", HttpStatus.BAD_REQUEST);
		}

		User user=userService.createUserItemByUserType(userType);
		user.setUsername(userEmail);
		user.setEmail(userEmail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserType(userType);
		user.setUserTypeSlug(toSlug(userType));

		//String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
		String encryptedPassword = SecurityUtility.randomPassword();
		//currentUser.setPassword(passwordEncoder.encode(newPassword));
		user.setPassword(passwordEncoder.encode(encryptedPassword));

		user=userService.createUser(user, userService.getUserRoles(user,userType));
		notificationService.notifyAllToRunMatchingAlgorithm(user.getUsername());

		SimpleMailMessage email = mailConstructor.constructNewUserEmail(user, encryptedPassword,true);
		mailSender.send(email);

		return new ResponseEntity(user, HttpStatus.OK);

	}	
	
	@RequestMapping(value = "/new_social", method = RequestMethod.POST)
	public ResponseEntity newSocialUser(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		String firstName=mapper.get("firstName");
		String lastName=mapper.get("lastName");
		String username = mapper.get("username");
		String userEmail = mapper.get("email");		
		String userType=mapper.get("userType");	
		
		
		if (userService.findByUsername(username) != null) {
			return new ResponseEntity("usernameExists", HttpStatus.BAD_REQUEST);
		}
		
		if (userService.findByEmail(userEmail) != null) {
			return new ResponseEntity("emailExists", HttpStatus.BAD_REQUEST);
		}

		User user=userService.createUserItemByUserType(userType);
		user.setUsername(userEmail);
		user.setEmail(userEmail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserType(userType);
		user.setRegisteredByProvider(true);
		user.setProfileImageSet(true);
		user.setUserTypeSlug(toSlug(userType));
		String password = "foundersrooms";

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		user=userService.createUser(user, userService.getUserRoles(user,userType));
		notificationService.notifyAllToRunMatchingAlgorithm(user.getUsername());
/*
		SimpleMailMessage email = mailConstructor.constructNewUserEmail(user, password);
		mailSender.send(email);*/

		return new ResponseEntity(user, HttpStatus.OK);

	}
	
	@RequestMapping(value="/check_password")
	public ResponseEntity checkPassword(@RequestBody HashMap<String,String> mapper, Principal principal){		
		String username=principal.getName();
		User currentUser=userService.findByUsername(username);
		String currentPassword = mapper.get("currentPassword");
		SecurityConfig securityConfig = new SecurityConfig();		
		BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
		String dbPassword = currentUser.getPassword();
		
			if (passwordEncoder.matches(currentPassword, dbPassword)) {
				return new ResponseEntity("OK", HttpStatus.OK);
			} else {
				return new ResponseEntity("Incorrect current password!", HttpStatus.BAD_REQUEST);
			}			
		
	}
	
	
	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public ResponseEntity newUserPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		String username = mapper.get("username");
		String userEmail = mapper.get("email");

		if (userService.findByUsername(username) != null) {
			return new ResponseEntity("usernameExists", HttpStatus.BAD_REQUEST);
		}

		if (userService.findByEmail(userEmail) != null) {
			return new ResponseEntity("emailExists", HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(userEmail);

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleID(1);
		role.setName("ROLE_CREATOR");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.createUser(user, userRoles);

		SimpleMailMessage email = mailConstructor.constructNewUserEmail(user, password,true);
		mailSender.send(email);

		return new ResponseEntity("User Added Successfully!", HttpStatus.OK);

	}
	
	@RequestMapping(value = "/update_password", method = RequestMethod.POST)
	public ResponseEntity updatePasswordPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper) {
		User currentUser = userService.findByUsername((String) mapper.get("username")) ;		
		String newPassword = (String) mapper.get("newPassword");
		String currentPassword = (String) mapper.get("currentPassword");							


		SecurityConfig securityConfig = new SecurityConfig();

		BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
		String dbPassword = currentUser.getPassword();

		if (null != currentPassword)
			if (passwordEncoder.matches(currentPassword, dbPassword)) {
				if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
					currentUser.setPassword(passwordEncoder.encode(newPassword));
					userService.save(currentUser);
				}				
			} else {
				return new ResponseEntity("Incorrect current password!", HttpStatus.BAD_REQUEST);
			}		
		return new ResponseEntity("Password Updated!", HttpStatus.OK);
	}

	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public ResponseEntity forgetPasswordPost(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {

		User user = userService.findByEmail(mapper.get("email"));

		if (user == null) {
			return new ResponseEntity("Email not found", HttpStatus.BAD_REQUEST);
		}
		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);
		userService.save(user);

		SimpleMailMessage newEmail = mailConstructor.constructNewUserEmail(user, password,false);
		mailSender.send(newEmail);

		return new ResponseEntity("Email sent!", HttpStatus.OK);

	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public ResponseEntity profileInfo(@RequestBody HashMap<String, Object> mapper) throws Exception {

		int id = (Integer) mapper.get("id");
		String email = (String) mapper.get("email");
		String username = (String) mapper.get("username");
		String newPassword = (String) mapper.get("newPassword");
		String currentPassword = (String) mapper.get("currentPassword");		

		User currentUser = userService.findById(Long.valueOf(id));				

		if (currentUser == null) {
			throw new Exception("User not found");
		}
		if(email!=null)
			if (userService.findByEmail(email) != null) {			
				if (userService.findByEmail(email).getId() != currentUser.getId()) {
					return new ResponseEntity("Email not found!", HttpStatus.BAD_REQUEST);
				}
			}

		if (userService.findByUsername(username) != null) {
			if (userService.findByUsername(username).getId() != currentUser.getId()) {
				return new ResponseEntity("Username not found!", HttpStatus.BAD_REQUEST);
			}
		}

		SecurityConfig securityConfig = new SecurityConfig();

		BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
		String dbPassword = currentUser.getPassword();

		if (null != currentPassword)
			if (passwordEncoder.matches(currentPassword, dbPassword)) {
				if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
					currentUser.setPassword(passwordEncoder.encode(newPassword));
				}
				currentUser.setEmail(email);
			} else {
				return new ResponseEntity("Incorrect current password!", HttpStatus.BAD_REQUEST);
			}
		userService.updateInMemoryUserItem(currentUser, mapper);
		userService.save(currentUser);
		
		User elasticUser=userService.findElasticUserByUserName(currentUser.getUsername());
		if(elasticUser==null) 
			elasticUser=userService.createElasticUser(currentUser);		
		userService.cloneUser(elasticUser,currentUser);		
		userService.elasticSave(elasticUser);
		//update project creator description & contact references
		if(currentUser instanceof Creator)
			projectService.updateProjectCreatorDescription((Creator)currentUser);
		contactService.updateContactsFollowingUserProfileChange(elasticUser);
		//notify all users to run matching algorithm
		notificationService.notifyAllToRunMatchingAlgorithm(currentUser.getUsername());
		
		return new ResponseEntity("Update Success", HttpStatus.OK);
	}

	@RequestMapping("/getCurrentUser")
	public User getCurrentUser(Principal principal) {
		User user = new User();
		if (null != principal) {
			user = userService.findByUsername(principal.getName());
			user.setInvitationItemsNumber(userService.getNumberOfUsersThatSentMeRequest(user.getId()));
			//user=userService.save(user);
		}
		return user;
	}
	
	@RequestMapping("/current_user")
	public User currentUser(Principal principal) {
		return userService.findElasticUserByUserName(principal.getName());

	}
/*
	@RequestMapping(value = "/add/avatar", method = RequestMethod.POST)
	public ResponseEntity upload(@RequestParam("id") String parameter, HttpServletResponse response,
			HttpServletRequest request) {
		//Long id=new Long(parameter.charAt(0));
		Long id=new Long(parameter.substring(0, 1));
		try {
			User user = userService.findOne(id);
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator<String> it = multipartRequest.getFileNames();
			MultipartFile multipartFile = multipartRequest.getFile(it.next());
			String fileName = id + ".jpg";

			byte[] bytes = multipartFile.getBytes();				
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/image/user/" + fileName)));
			stream.write(bytes);
			stream.close();
			user.setProfileImageSet(true);
			userService.save(user);
			return new ResponseEntity("Upload Success!", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Upload failed!", HttpStatus.BAD_REQUEST);
		}
	}*/
	
	@RequestMapping(value = "/add/avatar/{id}", method = RequestMethod.POST)
	public ResponseEntity upload(@PathVariable("id") Long parameter, HttpServletResponse response,
			HttpServletRequest request) {
		//Long id=new Long(parameter.charAt(0));
		//Long id=new Long(parameter.substring(0, 1));
		Long id=parameter;
		try {
			User user = userService.findOne(id);
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator<String> it = multipartRequest.getFileNames();
			MultipartFile multipartFile = multipartRequest.getFile(it.next());
			amazonS3Client.uploadFile(multipartFile, id.toString());
			/*String fileName = id + ".jpg";

			byte[] bytes = multipartFile.getBytes();				
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/image/user/" + fileName)));
			stream.write(bytes);
			stream.close();*/
			user.setProfileImageSet(true);
			userService.save(user);
			return new ResponseEntity("Upload Success!", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Upload failed!", HttpStatus.BAD_REQUEST);
		}
	}	
	

	@RequestMapping("/userList")
	public List<User> getAllUsers(Principal principal) {
		List<User> result = new ArrayList<>();
		String username = principal.getName();
		List<User> users = userService.findAll();
		for (User user : users)
			if (!user.getUsername().equals(username))
				result.add(user);

		return result;
	}
	
	@RequestMapping("/my_contacts")
	public ResponseEntity getAllMyContacts(Principal principal) {
		User user=userService.findByUsername(principal.getName());
		Set<User> contacts=user.getContacts();
		return new ResponseEntity(user.getContacts(),HttpStatus.OK);
	}
	
	@RequestMapping("my_contacts/{token}")
	public ResponseEntity getMyContactsWithMatchCriteria(@PathVariable("token") String token,Principal principal) {
		User user=userService.findByUsername(principal.getName());
		Set<User> resultSet=userService.findMyContactsByFirstNameUsernameOrLastName(token, token, token, user.getId());
		return new ResponseEntity(resultSet,HttpStatus.OK);
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ResponseEntity removeUser(@RequestBody String id) throws IOException {
		userService.removeOne(Long.parseLong(id));
		String fileName = id + ".png";

		Files.delete(Paths.get("src/main/resources/static/image/user/" + fileName));

		return new ResponseEntity("Remove Success!", HttpStatus.OK);
	}

	@RequestMapping("/profile/{id}")
	public UserTemp getUserWitchIsNotMe(@PathVariable("id") Long id, Principal principal) {
		/*User user = userService.findOne(id);
		User userLoggedIn = userService.findByUsername(principal.getName());*/
		User user=userService.findElasticUserById(id);
		if(user==null) {
			User mysqlUser=userService.findById(id);			
			user=userService.createElasticUser(mysqlUser);
			
		}
		User userLoggedIn =userService.findElasticUserByUserName(principal.getName());
				
		UserTemp userTemp = userService.clone(user, userLoggedIn.getId());

		if (userLoggedIn.getContacts().contains(user))
			userTemp.setAContact(true);
		if (notificationService.isNotificationAlreadySent(userLoggedIn, user))
			userTemp.setAContact(true);
		if(notificationService.isConnectionNotificationReceived(userLoggedIn, user))
			userTemp.setConnectionNotificationReceived(true);
		if(userLoggedIn.getId()==userTemp.getId())
			userTemp.setMe(true);
		
		return userTemp;
	}
	
	@RequestMapping("/user_profile/{user_ref}")
	public UserTemp getUserProfileWitchIsNotMine(@PathVariable("user_ref") String userRef, Principal principal) {
		/*User user = userService.findOne(id);
		User userLoggedIn = userService.findByUsername(principal.getName());*/
		User user=userService.findElasticUserByUsernameSlug(userRef);
		if(user==null) {
			User mysqlUser=userService.findUserByUsernameSlug(userRef);			
			user=userService.createElasticUser(mysqlUser);
			
		}
		User userLoggedIn =userService.findElasticUserByUserName(principal.getName());
				
		UserTemp userTemp = userService.clone(user, userLoggedIn.getId());

		if (userLoggedIn.getContacts().contains(user))
			userTemp.setAContact(true);
		if (notificationService.isNotificationAlreadySent(userLoggedIn, user))
			userTemp.setAContact(true);
		if(notificationService.isConnectionNotificationReceived(userLoggedIn, user))
			userTemp.setConnectionNotificationReceived(true);
		if(userLoggedIn.getId()==userTemp.getId())
			userTemp.setMe(true);
		
		return userTemp;
	}	
	
	@RequestMapping("/network")
	public UserWrapperForNetwork getUserNetwork(Principal principal) {		
		User user=userService.findByUsername(principal.getName());		
		return userService.getUserNetwork(user.getId());
		
	}
	

	
	@RequestMapping(value = "/persist_services", method = RequestMethod.POST)
	public String persistUserServices(Principal principal, @RequestBody String services)
			throws Exception {				
		User user=userService.findByUsername(principal.getName());	
		User elasticSuser=userService.findByUsername(user.getUsername());	
		
		Set<ServiceDetails> currentServicesDetails=utilityService.saveServiceDetails(services, user);
		
		//Set<ServiceDetails> elasticCurrentServicesDetails=utilityService.getServiceDetails(services, elasticSuser);
		Set<ServiceDetails> elasticMergedServicesDetails=utilityService.mergeElasticSearchServiceDetails(currentServicesDetails, user);		
		//elasticSuser.setServiceDetails(elasticMergedServicesDetails);  		
		
		String serviceType=((ServiceDetails)currentServicesDetails.toArray()[0]).getServiceType();
	
		utilityService.removeUnusedServiceDetailsSmartly(currentServicesDetails, user.getId(),serviceType);
		//utilityService.removeUnusedElasticServiceDetailsSmartly(services, elasticSuser.getId(), serviceType);
		
		//user.setServiceDetails(currentServicesDetails);		

		user=userService.save(user);		
		elasticSuser.setServiceDetails(elasticMergedServicesDetails);
		elasticSuser.setExpectations(utilityService.getExpectionsFromServiceDetails(user.getServiceDetails()));
		elasticSuser=userService.elasticSave(elasticSuser);
		
		//notify all users to run matching algorithm
		notificationService.notifyAllToRunMatchingAlgorithm(user.getUsername());
		
		return utilityService.getUserServicesDetailsByServiceType(user.getId(),serviceType);

	}
	
	@RequestMapping(value = "/load_user_services_details")
	public String getUserServicesDetails(Principal principal) {
		User user=userService.findByUsername(principal.getName());
		return utilityService.getAllUserServicesDetails(user.getId());
		
	}
	
	@RequestMapping(value="/delete_service_details/{service_type}", method = RequestMethod.DELETE)
	public ResponseEntity deleteServicesDetailsByServiceType(@PathVariable("service_type") String serviceType, Principal principal) {
		User user=userService.findByUsername(principal.getName());		
		utilityService.deleteServicesDetailsByServiceType(user.getId(), serviceType);
		
		//notify all users to run matching algorithm
		notificationService.notifyAllToRunMatchingAlgorithm(user.getUsername());
		
		return new ResponseEntity("Remove Success!", HttpStatus.OK);
	}	
	
	@RequestMapping(value="/not_yet_connected_to")
	public ResponseEntity findPeopleThatAreNotYetInMyContactsList(Principal principal) {
		User user=userService.findByUsername(principal.getName());
		return new ResponseEntity(userService.findPeopleThatAreNotYetInMyContactsList(user),HttpStatus.OK);
	}
	
	@RequestMapping(value="/contacts")
	public ResponseEntity getUserContacts(Principal principal) {
		User user=userService.findElasticUserByUserName(principal.getName());		
		return new ResponseEntity(user.getElasticContacts(),HttpStatus.OK);
	}	
		
	@RequestMapping(value="/is_basic_requirement_provided")
	public ResponseEntity checkBasicRequirements(Principal principal) {
		return new ResponseEntity(userService.isBasicRequirementsProvided(principal.getName()),HttpStatus.OK);
	}
}
