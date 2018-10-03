package com.foundersrooms.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import com.google.gson.stream.JsonReader;
import com.google.gson.reflect.TypeToken;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.location.City;
import com.foundersrooms.domain.people.Expectation;
import com.foundersrooms.domain.people.ServiceDetails;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.elasticsearchreprository.ServiceDetailsElasticRepository;
import com.foundersrooms.elasticsearchreprository.UserElasticRepository;
import com.foundersrooms.repository.CityRepository;
import com.foundersrooms.repository.ServiceDetailsRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.UtilityService;
import com.google.gson.Gson;

@Service
public class UtilityServiceImpl implements UtilityService {
	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private ServiceDetailsRepository serviceDetailsRepository;

	@Autowired
	private UserElasticRepository userElasticRepository;
	/*
	 * @Autowired private ServiceDetailsElasticRepository
	 * serviceDetailsElasticRepository;
	 */
	@Autowired
	private UserRepository userRepository;

	@Override
	public Set<City> findAllCities() {
		// TODO Auto-generated method stub
		Set<City> cities = new HashSet<>();
		Iterable<City> result = cityRepository.findAll();
		result.forEach(cities::add);
		return cities;
	}

	@Override
	public Set<City> findCitiesByCityName(String cityName) {
		// TODO Auto-generated method stub
		return cityRepository.findByNameStartsWithAllIgnoreCaseOrderByNameAsc(cityName);
	}

	@Override
	public boolean isCityExists(String value) {
		// TODO Auto-generated method stub
		if (cityRepository.countByName(value) > 0)
			return true;
		return false;
	}

	private class InnerCityTemplate {
		public boolean found = false;
		public String name = "";

	}

	public Object findCityByName(String value) {
		InnerCityTemplate temp = new InnerCityTemplate();
		List<City> cities = new ArrayList<City>();
		cities = cityRepository.findByNameAllIgnoreCase(value);
		if (!cities.isEmpty()) {
			temp.found = true;
			temp.name = cities.get(0).getName();
		}
		return temp;
	}

	private class ServicesWrapper {
		public Long idService;
		public String service;
		public String serviceType;
		public String details;
		public int[] value1 = new int[2];
		public int[] value2 = new int[2];
		public int value3;
		public boolean isDirty = false;// if dirty is true, instance is already exists. Another item should not be
										// created
	}

	private ServicesWrapper getRawDataBasedOnServiceDetails(ServiceDetails sd) {
		ServicesWrapper sw = new ServicesWrapper();
		sw.idService = sd.getId();
		sw.service = sd.getServiceName();
		sw.serviceType = sd.getServiceType();
		sw.details = sd.getServiceField().getDescription();
		sw.value1[0] = sd.getServiceField().getMinDuration();
		sw.value1[1] = sd.getServiceField().getMaxDuration();
		sw.value2[0] = sd.getMinAmount();
		sw.value2[1] = sd.getMaxAmount();
		sw.value3 = sd.getRate();
		sw.isDirty = true;
		return sw;
	}

	private ServiceDetails getServiceDetailsBasedOnRawData(ServicesWrapper sw, User user) {
		ServiceDetails sd = new ServiceDetails();
		if (sw.isDirty)
			sd.setId(sw.idService);
		sd.setServiceName(sw.service);
		sd.setServiceNameSlug(toSlug(sw.service));
		sd.setServiceType(sw.serviceType);
		sd.getServiceField().setDescription(sw.details);
		sd.getServiceField().setMinDuration(sw.value1[0]);
		sd.getServiceField().setMaxDuration(sw.value1[1]);
		sd.setMinAmount(sw.value2[0]);
		sd.setMaxAmount(sw.value2[1]);
		sd.setRate(sw.value3);
		sd.setUser(user);
		return sd;

	}

	@Override
	public Set<ServiceDetails> saveServiceDetails(String details, User user) {
		// TODO Auto-generated method stub
		User elasticUser = userElasticRepository.findByUsername(user.getUsername());
		Gson gson = new Gson();
		ServicesWrapper[] services = gson.fromJson(details, ServicesWrapper[].class);
		Set<ServiceDetails> userServicesDetails = new HashSet<>();
		for (ServicesWrapper item : services) {
			ServiceDetails sd = getServiceDetailsBasedOnRawData(item, user);
			serviceDetailsRepository.save(sd);
			// serviceDetailsElasticRepository.save(sd);
			userServicesDetails.add(sd);
		}
		userRepository.save(user);
		return userServicesDetails;
	}

	class TempClass {
		public List<String> services = new ArrayList<String>();
		public Set<ServicesWrapper> needs = new HashSet<>();
	}

	@Override
	public String getUserServicesDetailsByServiceType(Long userId, String serviceType) {
		TempClass temp = new TempClass();
		Set<ServicesWrapper> wrapper = new HashSet<>();
		Set<ServiceDetails> userServicesDetails = serviceDetailsRepository.getUserServicesDetailsByServiceType(userId,
				serviceType);
		for (ServiceDetails sd : userServicesDetails) {
			temp.services.add(sd.getServiceName());
			wrapper.add(getRawDataBasedOnServiceDetails(sd));
		}
		temp.needs = wrapper;
		Gson gson = new Gson();
		return gson.toJson(temp);
	}

	private TempClass formatNeedDetailsResults(Set<ServiceDetails> userServicesDetails) {
		Set<ServicesWrapper> wrapper = new HashSet<>();
		TempClass temp = new TempClass();
		for (ServiceDetails sd : userServicesDetails) {
			temp.services.add(sd.getServiceName());
			wrapper.add(getRawDataBasedOnServiceDetails(sd));
		}
		temp.needs = wrapper;
		return temp;
	}

	@Override
	public String getAllUserServicesDetails(Long userId) {
		TempClass[] result = new TempClass[4];
		Set<ServiceDetails> userServicesDetails /*= serviceDetailsRepository.getUserServicesDetailsByServiceType(userId,
				"investment")*/;
		result[0] = formatNeedDetailsResults(serviceDetailsRepository
				.getUserServicesDetailsByServiceType(userId, "investment"));
		result[1] = formatNeedDetailsResults(serviceDetailsRepository.getUserServicesDetailsByServiceType(userId, "coaching"));
		result[2] = formatNeedDetailsResults(serviceDetailsRepository
				.getUserServicesDetailsByServiceType(userId, "operations"));
		result[3] = formatNeedDetailsResults( serviceDetailsRepository.getUserServicesDetailsByServiceType(userId, "advise"));
		// TempClass temp=formatNeedDetailsResults(userServicesDetails);
		Gson gson = new Gson();
		return gson.toJson(result);
	}

	@Transactional
	@Override
	public void removeUnusedServiceDetailsSmartly(Set<ServiceDetails> currentServicesDetails, Long userId,
			String serviceType) {
		serviceDetailsRepository.removeUnusedServicesDetailsItems(userId, currentServicesDetails, serviceType);

	}

	@Transactional
	@Override
	public void deleteServicesDetailsByServiceType(Long userId, String serviceType) {
		// TODO Auto-generated method stub
		serviceDetailsRepository.deleteServicesDetailsByServiceType(userId, serviceType);
		User elasticUser = userElasticRepository.findOne(userId);
		/*
		 * for(ServiceDetails sd:elasticUser.getServiceDetails())
		 * if(sd.getServiceType().equals(serviceType))
		 * elasticUser.getServiceDetails().remove(sd);
		 */
		for (Expectation item : elasticUser.getExpectations())
			if (item.getServiceType().equals(serviceType))
				elasticUser.getExpectations().remove(item);
		userElasticRepository.save(elasticUser);
	}

	public static String toSlug(String input) {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	Set<String> getServicesLabel(ServicesWrapper[] wrapper) {
		Set<String> result = new HashSet();
		for (ServicesWrapper item : wrapper) {
			result.add(item.service);
		}
		return result;
	}

	@Transactional
	@Override
	public void removeUnusedElasticServiceDetailsSmartly(String services, Long userId, String serviceType) {
		Gson gson = new Gson();
		ServicesWrapper[] wrapper = gson.fromJson(services, ServicesWrapper[].class);
		User user = userElasticRepository.findOne(userId);
		Set<String> comparisationArray = getServicesLabel(wrapper);
		for (ServiceDetails sd : user.getServiceDetails()) {
			if (sd.getServiceType().equals(serviceType) && !comparisationArray.contains(sd.getServiceName()))
				;
			user.getServiceDetails().remove(sd);
		}
	}

	@Override
	public Set<ServiceDetails> getServiceDetails(String services, User user) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		ServicesWrapper[] wrapper = gson.fromJson(services, ServicesWrapper[].class);
		Set<ServiceDetails> result = new HashSet();
		for (ServicesWrapper item : wrapper) {
			ServiceDetails sd = new ServiceDetails();
			sd.setServiceName(item.service);
			sd.setServiceNameSlug(toSlug(item.service));
			sd.setServiceType(item.serviceType);
			sd.setServiceType(item.serviceType);
			sd.getServiceField().setDescription(item.details);
			sd.getServiceField().setMinDuration(item.value1[0]);
			sd.getServiceField().setMaxDuration(item.value1[1]);
			sd.setMinAmount(item.value2[0]);
			sd.setMaxAmount(item.value2[1]);
			sd.setRate(item.value3);
			// sd.setUser(user);
			result.add(sd);
		}
		return result;
	}

	@Override
	public Set<ServiceDetails> mergeElasticSearchServiceDetails(Set<ServiceDetails> serviceDetails, User user) {
		// TODO Auto-generated method stub
		// Set<ServiceDetails> dbItems=user.getServiceDetails();
		Set<ServiceDetails> aggregate = user.getServiceDetails();
		/*
		 * for (ServiceDetails item : dbItems) { if(serviceDetails.contains(item)) {
		 * for(ServiceDetails sd:serviceDetails) {
		 * if(sd.getServiceName().equals(item.getServiceName())) { item=sd; break; } }
		 * }else { dbItems.remove(item); }
		 * 
		 * }
		 */
		aggregate.addAll(serviceDetails);
		return aggregate;
	}

	@Override
	public List<Expectation> getExpectionsFromServiceDetails(Set<ServiceDetails> services) {
		// TODO Auto-generated method stub
		List<Expectation> result = new ArrayList();
		for (ServiceDetails sd : services) {
			Expectation item = new Expectation();
			item.setId(sd.getId());
			item.setServiceName(sd.getServiceName());
			item.setServiceNameSlug(toSlug(sd.getServiceName()));
			item.setServiceType(sd.getServiceType());
			item.setDescription(sd.getServiceField().getDescription());
			item.setMaxAmount(sd.getMaxAmount());
			item.setMinAmount(sd.getMinAmount());
			item.setMaxDuration(sd.getServiceField().getMaxDuration());
			item.setMinDuration(sd.getServiceField().getMinDuration());
			item.setRate(sd.getRate());
			result.add(item);
		}
		return result;
	}

	@Override
	public List<String> loadSectors() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		List<String> result = new ArrayList<String>();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader("src/main/resources/static/json/sectors.json"));
			result = gson.fromJson(reader, new TypeToken<List<String>>() {
			}.getType());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;

	}

	@Override
	public List<List<String>> loadJobs() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		List<List<String>> result = new ArrayList();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader("src/main/resources/static/json/jobs.json"));
			result = gson.fromJson(reader, new TypeToken<List<List<String>> >() {
			}.getType());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;
	}

	@Override
	public List<List<String>> loadServices() {
		Gson gson = new Gson();
		List<List<String>> result = new ArrayList();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader("src/main/resources/static/json/services.json"));
			result = gson.fromJson(reader, new TypeToken<List<List<String>> >() {
			}.getType());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return result;
	}

}
