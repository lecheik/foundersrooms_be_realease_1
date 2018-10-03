package com.foundersrooms.domain.people;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersrooms.domain.Notification;
import com.foundersrooms.domain.UserActivity;
import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.domain.security.Authority;
import com.foundersrooms.domain.security.UserRole;
import com.foundersrooms.service.impl.UtilityServiceImpl;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Document(indexName = "foundersrooms", type = "users")
public class User implements UserDetails, Serializable {

	private static final long serialVersionUID = 902783495L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;

	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String username;
	@JsonIgnore
	private String password;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String firstName;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String lastName;
	@Transient
	private String firstNameAndLastName;
	//@JsonIgnore
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String completeName="";
	private String userNameSlug="";
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String email;
	private String phone;
	private boolean enabled = true;
	
	private String bio;
	private String codePostal;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String facebookID;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String googleID;
	private String intro;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String linkedInID;
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String tweeterId;
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true)
	private String country;
	private String town="";
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true, analyzer = "standard")
	private String townSlug="";
	private String sector="";
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true)
	private String sectorSlug="";
	@Field(type = FieldType.String, index = FieldIndex.analyzed, store = true)
	private String job;
	@Transient
	private boolean isMe=false;
	@Field(type = FieldType.Boolean, index = FieldIndex.not_analyzed, store = true)
	private boolean registeredByProvider=false;
	private boolean profileImageSet=false;
	private boolean online=false;
	@Transient
	private Set<String> userTypeParameter= new HashSet();
	 
	/*
	@Transient
	@Field(type=FieldType.Nested)
	private List<Author> authors;
	*/
	
	//@Transient
	private String userType="";
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
	private String userTypeSlug;
	
	@Transient
	private Set<String> sectorsToSearch=new HashSet();
	
	@Transient
	private int invitationItemsNumber;
	
	private String avatar;	
	
	public String getUserNameSlug() {
		return userNameSlug;
	}

	public void setUserNameSlug(String userNameSlug) {
		this.userNameSlug = userNameSlug;
	}

	public String getSectorSlug() {
		return sectorSlug;
	}


	public String getTownSlug() {
		return townSlug;
	}


	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
	}


	public void setTownSlug(String townSlug) {
		this.townSlug = townSlug;
	}


	public String getUserTypeSlug() {
		return userTypeSlug;
	}


	public void setUserTypeSlug(String userTypeSlug) {
		this.userTypeSlug = userTypeSlug;
	}


	public void setSectorSlug(String sectorSlug) {
		this.sectorSlug = sectorSlug;
	}


	public Set<String> getUserTypeParameter() {
		return userTypeParameter;
	}


	public void setUserTypeParameter(Set<String> userTypeParameter) {
		this.userTypeParameter = userTypeParameter;
	}


	public Set<String> getSectorsToSearch() {
		
		return sectorsToSearch;
	}


	public void setSectorsToSearch(Set<String> sectorsToSearch) {
		this.sectorsToSearch = sectorsToSearch;
	}


	public String getCompleteName() {
		return completeName;
	}


	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}


	public String getFirstNameAndLastName() {
		return firstNameAndLastName;
	}

	public void setFirstNameAndLastName(String firstNameAndLastName) {
		this.firstNameAndLastName = firstNameAndLastName;
	}


	@PrePersist
	@PostLoad
	@PreUpdate
	public void setType() {
		firstNameAndLastName=firstName+" "+lastName;
		if(this instanceof Investor)
			userType="Investisseur";
		if(this instanceof Provider)
			userType="Prestataire";
		if(this instanceof Creator)
			userType="Créateur";
		this.invitationItemsNumber=this.requetes.size();	
		completeName=UtilityServiceImpl.toSlug(firstName.toLowerCase()+" "+lastName.toLowerCase());
		userNameSlug=completeName+id;
		sectorSlug=UtilityServiceImpl.toSlug(sector);
		userTypeSlug=UtilityServiceImpl.toSlug(userType);
		if(town!="")
			townSlug=UtilityServiceImpl.toSlug(town);
	}	
	
	
	public boolean isProfileImageSet() {
		return profileImageSet;
	}

	public void setProfileImageSet(boolean profileImageSet) {
		this.profileImageSet = profileImageSet;
	}

	public boolean isRegisteredByProvider() {
		return registeredByProvider;
	}

	public void setRegisteredByProvider(boolean registeredByProvider) {
		this.registeredByProvider = registeredByProvider;
	}

	public int getInvitationItemsNumber() {
		return invitationItemsNumber;
	}

	public void setInvitationItemsNumber(int invitationItemsNumber) {
		this.invitationItemsNumber = invitationItemsNumber;
	}

	public String getSector() {
		return sector;
	}


	public void setSector(String sector) {
		this.sector = sector;
	}


	public boolean isMe() {
		return isMe;
	}

	public void setMe(boolean isMe) {
		this.isMe = isMe;
	}

	public String getTweeterId() {
		return tweeterId;
	}

	public void setTweeterId(String tweeterId) {
		this.tweeterId = tweeterId;
	}

	public String getJob() {
		return job;
	}


	public void setJob(String job) {
		this.job = job;
	}


	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public Set<ElasticContact> getElasticContacts() {
		return elasticContacts;
	}


	public void setElasticContacts(Set<ElasticContact> elasticContacts) {
		this.elasticContacts = elasticContacts;
	}

	@Transient
	@Field( type = FieldType.Nested)	
	private List<Expectation> expectations=new ArrayList<Expectation>();
	/*
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="owner")
	@OrderBy("id ASC")
	@JsonIgnore
	private Set<Chat> chats;
	*/
	/*
	@Transient
	@Field( type = FieldType.Nested)	
	private List<Chat> exchanges;
	*/
	@Field( type = FieldType.Nested, store = true)
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="user")
	@OrderBy("serviceName ASC")
	@JsonIgnore
	private Set<ServiceDetails> serviceDetails=new HashSet<>();
	
	@OneToMany(mappedBy = "notifier", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Notification> requetes=new HashSet<>();
	
	@OneToMany(mappedBy = "guest", cascade=CascadeType.ALL,fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Notification> invitations=new HashSet<>();
		
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<UserActivity> secteurActivite=new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<UserRole> userRoles = new HashSet<>();

	@OneToMany(mappedBy="member",fetch=FetchType.LAZY)
	@JsonIgnore
	private Set<ProjectMember> involVment=new HashSet<>();
	
	@ManyToMany(cascade = {CascadeType.REMOVE},fetch=FetchType.LAZY)	
	@JoinTable(name="user_contacts",joinColumns=@JoinColumn(name="user_id",unique=false,referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="contact_id",unique=false,referencedColumnName="id"))	
	@JsonIgnore
	private Set<User> contacts=new HashSet<>();	
	
	@Transient
	@Field(type=FieldType.Nested)
	private Set<ElasticContact> elasticContacts=new HashSet<>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Set<ServiceDetails> getServiceDetails() {
		return serviceDetails;
	}


	public void setServiceDetails(Set<ServiceDetails> serviceDetails) {
		this.serviceDetails = serviceDetails;
	}


	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public Set<ProjectMember> getInvolVment() {
		return involVment;
	}

	public void setInvolVment(Set<ProjectMember> involVment) {
		this.involVment = involVment;
	}

	public Set<User> getContacts() {
		return contacts;
	}

	public void setContacts(Set<User> contacts) {
		this.contacts = contacts;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Set<Notification> getRequetes() {
		return requetes;
	}

	public void setRequetes(Set<Notification> requetes) {
		this.requetes = requetes;
	}

	public Set<Notification> getInvitations() {
		return invitations;
	}

	public void setInvitations(Set<Notification> invitations) {
		this.invitations = invitations;
	}

	public Set<UserActivity> getSecteurActivite() {
		return secteurActivite;
	}

	public void setSecteurActivite(Set<UserActivity> secteurActivite) {
		this.secteurActivite = secteurActivite;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getCodePostal() {
		return codePostal;
	}

	public List<Expectation> getExpectations() {
		return expectations;
	}


	public void setExpectations(List<Expectation> expectations) {
		this.expectations = expectations;
	}


	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getFacebookID() {
		return facebookID;
	}

	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getLinkedInID() {
		return linkedInID;
	}

	public void setLinkedInID(String linkedInID) {
		this.linkedInID = linkedInID;
	}

	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Set<GrantedAuthority> authorities = new HashSet<>();
		userRoles.forEach(ur -> authorities.add(new Authority(ur.getRole().getName())));

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		/*if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;*/
		User other = (User) obj;
		return username.equals(other.getUsername());
	}


	@Override
	public String toString() {
		return "User [townSlug=" + townSlug + ", sector=" + sector + ", sectorSlug=" + sectorSlug + ", userType="
				+ userType + ", userTypeSlug=" + userTypeSlug + ", expectations=" + expectations + "]";
	}
			
}
