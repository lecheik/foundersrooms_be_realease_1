package com.foundersrooms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cloudfront.model.QueryStringCacheKeys;
import com.foundersrooms.domain.people.ElasticContact;
import com.foundersrooms.domain.people.Expectation;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.elasticsearchreprository.ContactRepository;
import com.foundersrooms.elasticsearchreprository.UserElasticRepository;
import com.foundersrooms.repository.ProjectRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserElasticRepository userElasticRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Override
	public Set<User> searchElasticUsers(User userTemplate, User me) {
		BoolQueryBuilder completebuilder=new BoolQueryBuilder();
		BoolQueryBuilder userTypebuilder=new BoolQueryBuilder();
		BoolQueryBuilder sectorsBuilder=new BoolQueryBuilder();
		completebuilder
			.mustNot(
				matchQuery("username",me.getUsername())
				);
		if (userTemplate.getCompleteName().toLowerCase().trim().length() != 0)
			completebuilder
				.must(
						queryStringQuery(UtilityServiceImpl.toSlug(userTemplate.getCompleteName().toLowerCase().trim()))
							.analyzeWildcard(true).field("completeName")
						);

		if (userTemplate.getTown().toLowerCase().trim().length() != 0)
			completebuilder
				.must(
						matchQuery("townSlug",UtilityServiceImpl.toSlug(userTemplate.getTown()))
						);
		
		for (String item : userTemplate.getUserTypeParameter()) 
			userTypebuilder.should(matchQuery("userTypeSlug",item));
		
		if (userTemplate.getSectorsToSearch().size() != 0)			
			for (String item : userTemplate.getSectorsToSearch()) 
				sectorsBuilder.should(matchQuery("sectorSlug",UtilityServiceImpl.toSlug(item)));
			
		if (userTemplate.getUserTypeParameter().size() > 0 && userTemplate.getSectorsToSearch().size() == 0)
			completebuilder.must(userTypebuilder);

		if (userTemplate.getSectorsToSearch().size() > 0 && userTemplate.getUserTypeParameter().size() == 0)
			completebuilder.must(sectorsBuilder);
		
		SearchQuery build = new NativeSearchQueryBuilder()
				.withQuery(completebuilder)
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC)).build();		
		List<User> result = elasticsearchTemplate.queryForList(build, User.class);
		//result.remove(me);
		return new HashSet<User>(result);
	}	

	@Override
	public Set<User> searchUsers(User user, User me) {
		// TODO Auto-generated method stub
		Specification<User> specification = new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				List<Predicate> userTypePredicate = new ArrayList<Predicate>();
				List<Predicate> sectorPredicate = new ArrayList<Predicate>();

				if (user.getCompleteName().toLowerCase().trim().length() != 0)
					predicates.add(builder.like(root.<String>get("completeName"),
							"%" + UtilityServiceImpl.toSlug(user.getCompleteName().toLowerCase().trim()) + "%"));
				/*
				 * if(user.getSector().toLowerCase().trim().length()!=0)
				 * predicates.add(builder.equal(root.<String>get("sectorSlug"),
				 * UtilityServiceImpl.toSlug(user.getSector())));
				 */
				if (user.getTown().toLowerCase().trim().length() != 0)
					predicates.add(
							builder.equal(root.<String>get("townSlug"), UtilityServiceImpl.toSlug(user.getTown())));

				for (String item : user.getUserTypeParameter()) {
					userTypePredicate.add(builder.equal(root.<String>get("userTypeSlug"), item));
				}
				if (user.getSectorsToSearch().size() != 0)
					for (String item : user.getSectorsToSearch()) {
						sectorPredicate
								.add(builder.equal(root.<String>get("sectorSlug"), UtilityServiceImpl.toSlug(item)));
					}

				Predicate and = builder.and(predicates.toArray(new Predicate[predicates.size()]));
				Predicate orUserType = builder.or(userTypePredicate.toArray(new Predicate[userTypePredicate.size()]));
				Predicate orSector = builder.or(sectorPredicate.toArray(new Predicate[sectorPredicate.size()]));
				Predicate or = builder.and(orUserType, orSector);

				if (user.getUserTypeParameter().size() > 0 && user.getSectorsToSearch().size() > 0)
					return builder.and(and, or);
				if (user.getUserTypeParameter().size() > 0 && user.getSectorsToSearch().size() == 0)
					return builder.and(and, orUserType);
				if (user.getSectorsToSearch().size() > 0 && user.getUserTypeParameter().size() == 0)
					return builder.and(and, orSector);
				/*
				 * if(user.getUserTypeParameter().size()>0) return builder.and(and,or);
				 */
				return and;
			}
		};
		List<User> result = userRepository.findAll(specification);
		result.remove(me);
		return new HashSet<User>(result);
	}
	
	@Override
	public Set<Project> searchElasticProjects(Project projectTemplate) {
		// TODO Auto-generated method stub
		BoolQueryBuilder completeBuilder=new BoolQueryBuilder();
		BoolQueryBuilder sectorsBuilder=new BoolQueryBuilder();
		BoolQueryBuilder projectStepsBuilder=new BoolQueryBuilder();
		BoolQueryBuilder teamBuilder=new BoolQueryBuilder();
		
		User creator = projectTemplate.getCreator();
		
		if (projectTemplate.getName().toLowerCase().trim().length() != 0)
			completeBuilder
			.must(
					queryStringQuery(UtilityServiceImpl.toSlug(projectTemplate.getName().toLowerCase().trim()))
						.analyzeWildcard(true).field("name")
					);

		if (creator.getTown().toLowerCase().trim().length() != 0)
			completeBuilder
			.must(matchQuery("creator.townSlug",UtilityServiceImpl.toSlug(creator.getTown())));
		
		if (creator.getSectorsToSearch().size() != 0)
			for (String item : creator.getSectorsToSearch())
				sectorsBuilder.should(matchQuery("creator.sectorSlug",UtilityServiceImpl.toSlug(item)));
		
		projectStepsBuilder
			.must(rangeQuery("currentStepNum").lte(projectTemplate.getStepSearchSup()).gte(projectTemplate.getStepSearchInf()));		
		
		teamBuilder
			.must(rangeQuery("teamSize").lte(projectTemplate.getTeamSizeSearchSup()).gte(projectTemplate.getTeamSizeSearchInf()));
		
		if (creator.getSectorsToSearch().size() > 0) 
			completeBuilder.must(sectorsBuilder).must(projectStepsBuilder).must(teamBuilder);		
		else
			completeBuilder.must(projectStepsBuilder).must(teamBuilder);
		
		SearchQuery build = new NativeSearchQueryBuilder()
				.withQuery(completeBuilder)
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC)).build();		
		List<Project> result = elasticsearchTemplate.queryForList(build, Project.class);
		return new HashSet<Project>(result);
	}	

	@Override
	public Set<Project> searchProjects(Project projectTemplate) {
		// TODO Auto-generated method stub
		Specification<Project> specification = new Specification<Project>() {
			@Override
			public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				User creator = projectTemplate.getCreator();
				List<Predicate> userPredicates = new ArrayList<Predicate>();
				List<Predicate> projectStepPredicates = new ArrayList<Predicate>();
				List<Predicate> teamSizePredicates = new ArrayList<Predicate>();
				List<Predicate> sectorPredicates = new ArrayList<Predicate>();

				if (projectTemplate.getName().toLowerCase().trim().length() != 0)
					userPredicates.add(builder.like(root.<String>get("nameSlug"),
							"%" + UtilityServiceImpl.toSlug(projectTemplate.getName().toLowerCase().trim()) + "%"));
				if (creator.getTown().toLowerCase().trim().length() != 0)
					userPredicates.add(builder.equal(root.<User>get("creator").<String>get("townSlug"),
							UtilityServiceImpl.toSlug(creator.getTown())));
				/*
				 * if (creator.getTown().toLowerCase().trim().length() != 0)
				 * userPredicates.add(builder.equal(root.<String>get("creatorTown"),
				 * UtilityServiceImpl.toSlug(creator.getTown())));
				 */
				if (creator.getSectorsToSearch().size() != 0)
					for (String item : creator.getSectorsToSearch())
						sectorPredicates.add(builder.equal(root.<User>get("creator").<String>get("sectorSlug"),
								UtilityServiceImpl.toSlug(item)));
				projectStepPredicates.add(builder.greaterThanOrEqualTo(root.<Integer>get("currentStepNum"),
						projectTemplate.getStepSearchInf()));
				projectStepPredicates.add(builder.lessThanOrEqualTo(root.<Integer>get("currentStepNum"),
						projectTemplate.getStepSearchSup()));
				/*
				 * teamSizePredicates.add(builder.greaterThanOrEqualTo(root.<Integer>get(
				 * "teamSize"), projectTemplate.getTeamSizeSearchInf()));
				 * teamSizePredicates.add(builder.lessThanOrEqualTo(root.<Integer>get("teamSize"
				 * ), projectTemplate.getTeamSizeSearchSup()));
				 */
				teamSizePredicates.add(builder.greaterThanOrEqualTo(builder.size(root.<HashSet>get("members")),
						projectTemplate.getTeamSizeSearchInf()));
				teamSizePredicates.add(builder.lessThanOrEqualTo(builder.size(root.<HashSet>get("members")),
						projectTemplate.getTeamSizeSearchSup()));
				Predicate projectStepPredicate = builder
						.and(projectStepPredicates.toArray(new Predicate[projectStepPredicates.size()]));
				Predicate teamSizePredicate = builder
						.and(teamSizePredicates.toArray(new Predicate[teamSizePredicates.size()]));
				Predicate userPredicate = builder.and(userPredicates.toArray(new Predicate[userPredicates.size()]));
				Predicate sectorPredicate = builder
						.or(sectorPredicates.toArray(new Predicate[sectorPredicates.size()]));
				query.orderBy(builder.asc(root.get("nameSlug")));
				if (creator.getSectorsToSearch().size() > 0)
					return builder.and(userPredicate, sectorPredicate, projectStepPredicate, teamSizePredicate);

				return builder.and(userPredicate, projectStepPredicate, teamSizePredicate);
			}
		};
		List<Project> result = projectRepository.findAll(specification);
		return new HashSet<Project>(result);
	}

	private List<String> getSearchProfileTypes(String currentUserProfile) {
		List<String> creator = new ArrayList<String>(), supplier = new ArrayList<String>(),
				investor = new ArrayList<String>();

		creator.add("investisseur");
		creator.add("prestataire");

		supplier.add("createur");

		investor.add("createur");
		
		switch (currentUserProfile) {
		case "createur":
			return creator;

		case "prestataire":
			return supplier;

		default:
			return investor;
		}
	}	
	private QueryBuilder buildUserSearchQueryBuilder(String payLoad, String loggedInUsername) {
		BoolQueryBuilder bool = new BoolQueryBuilder();
		/*
		bool.should(queryStringQuery(payLoad+"*").analyzeWildcard(true)
				.field("elasticContacts.firstName").field("elasticContacts.firstName",2.0f).field("elasticContacts.username"));
		QueryBuilder builder = nestedQuery("elasticContacts", bool);				 
		return builder;*/
		/*bool.should(queryStringQuery(payLoad+"*").analyzeWildcard(true)
				.field("firstName").field("lastName",2.0f).field("username"));*/
		System.out.println(loggedInUsername);
		bool.must(matchQuery("ownerUsername",loggedInUsername));
		return bool;		
	}

	private QueryBuilder getExpectationBuilder(List<Expectation> expections) {
		BoolQueryBuilder bool = new BoolQueryBuilder();

		for (Expectation expect : expections) {

			BoolQueryBuilder builderItem = new BoolQueryBuilder();
			builderItem.must(matchQuery("expectations.serviceNameSlug", expect.getServiceNameSlug()).boost(10f))
			
			 .filter(rangeQuery("expectations.maxAmount").lte(expect.getMaxAmount()).gt(expect.getMinAmount()))
			 //.should(rangeQuery("expectations.minAmount").gte(sd.getMinAmount()).lt(sd.getMaxAmount()))
			 .filter(rangeQuery("expectations.minAmount").lte(expect.getMinAmount()))
			 .filter(rangeQuery("expectations.maxDuration").lte(expect.getMaxDuration()).gt(expect.getMinDuration()))
			 //.should(rangeQuery("expectations.minDuration").gte(sd.getServiceField().getMinDuration()).lt(sd.getServiceField().getMaxDuration()))
			  .filter(rangeQuery("expectations.minDuration").lte(expect.getMinDuration()))
			 .filter(rangeQuery("expectations.rate").lte(expect.getRate()))
			 // .minimumShouldMatch("1")
			 
			;
			bool.should(builderItem);
		}
		QueryBuilder builder = nestedQuery("expectations", bool)/*.boost(5f)*/;

		return builder;
	}

	private QueryBuilder getDetailsServicesBuilder(Set<ServiceDetails> services) {
		// QueryBuilder bool=null;
		BoolQueryBuilder bool = new BoolQueryBuilder();

		for (ServiceDetails sd : services) {
			BoolQueryBuilder builderItem = new BoolQueryBuilder();
			builderItem.must(matchQuery("expectations.serviceNameSlug", sd.getServiceNameSlug()).boost(2.5f))
			/*
			 * .should(rangeQuery("expectations.maxAmount").lte(sd.getMaxAmount()).gt(sd.
			 * getMinAmount()))
			 * //.should(rangeQuery("expectations.minAmount").gte(sd.getMinAmount()).lt(sd.
			 * getMaxAmount()))
			 * .should(rangeQuery("expectations.minAmount").lte(sd.getMinAmount()))
			 * .should(rangeQuery("expectations.maxDuration").lte(sd.getServiceField().
			 * getMaxDuration()).gt(sd.getServiceField().getMinDuration()))
			 * //.should(rangeQuery("expectations.minDuration").gte(sd.getServiceField().
			 * getMinDuration()).lt(sd.getServiceField().getMaxDuration()))
			 * .should(rangeQuery("expectations.minDuration").lte(sd.getServiceField().
			 * getMinDuration())) .should(rangeQuery("expectations.rate").lte(sd.getRate()))
			 */
			// .minimumShouldMatch("1")
			;
			bool.should(builderItem);
		}
		QueryBuilder builder = nestedQuery("expectations", bool)/*.boost(5f)*/;

		return builder;
	}

	private QueryBuilder buildQueryWithExpectations(String location, String sector, List<Expectation> expectations,
			String userType) {

		BoolQueryBuilder bool = new BoolQueryBuilder();

		List<String> profileTypes = getSearchProfileTypes(userType);
		for (String profile : profileTypes) 
			bool.should(matchQuery("userTypeSlug", profile).boost(2f));
		if (expectations != null)
			if (expectations.size() != 0)
				bool.should(getExpectationBuilder(expectations));
		if (location != null)
			if (location.length()>0)
				bool.should(matchQuery("townSlug", location));
		if (sector != null)
			if (sector.length()>0)
				bool.should(matchQuery("sectorSlug", sector).boost(1.5f));
		
		return bool;
	}
	

	private QueryBuilder getServiceDetailsBuilder(String location, String sector, Set<ServiceDetails> services) {
		BoolQueryBuilder bool = new BoolQueryBuilder();
		if (services.size() != 00)
			bool.should(getDetailsServicesBuilder(services)).boost(20f);
		if (location != null && !location.equals(""))
			bool.should(matchQuery("townSlug", location));
		if (sector != null && !sector.equals(""))
			bool.should(matchQuery("sectorSlug", sector).boost(1.5f));
		return bool;
	}

	@Override
	public List<User> getUserProfileMatching(User user) {
		Set<User> contacts = userRepository.findByUsername(user.getUsername()).getContacts();
		SearchQuery build = new NativeSearchQueryBuilder()
				// .withQuery(getServiceDetailsBuilder(user.getTown(),user.getSectorSlug(),user.getServiceDetails()))
				.withQuery(buildQueryWithExpectations(user.getTownSlug().trim(), user.getSectorSlug().trim(),
						user.getExpectations(), user.getUserTypeSlug()))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC)).build();

		List<User> result = elasticsearchTemplate.queryForList(build, User.class);
		List<User> users = new ArrayList<User>();
		users.addAll(result);
		users.remove(user);
		users.removeAll(contacts);
		return users;
	}

	@Override
	public List<ElasticContact> searchContactsWhoseCompleteNameStartsWith(String payLoad,String loggedInUsername) {
		payLoad=UtilityServiceImpl.toSlug(payLoad);
		//return userRepository.findByCompleteNameContainingAllIgnoreCaseOrderByLastNameAsc(payLoad);
		//return new ArrayList<User>(userElasticRepository.findByElasticContacts_completeNameContainingAllIgnoreCaseOrderByLastNameAsc(payLoad));
		return new ArrayList<ElasticContact>(contactRepository.findByOwnerUsernameAndCompleteNameContainingAllIgnoreCase(loggedInUsername, payLoad));
		/*
		SearchQuery build = new NativeSearchQueryBuilder()
				.withQuery(buildUserSearchQueryBuilder(payLoad,loggedInUsername))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC)).build();

		List<ElasticContact> result = elasticsearchTemplate.queryForList(build, ElasticContact.class);
		List<ElasticContact> users = new ArrayList<ElasticContact>();
		users.addAll(result);	
		return result;*/
		
	}

}
