package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.people.ServiceDetails;

public interface ServiceDetailsRepository extends CrudRepository<ServiceDetails, Long> {

	@Query("SELECT sd FROM ServiceDetails sd WHERE  sd.user.id=:myId ORDER BY sd.serviceName ASC")	
	public Set<ServiceDetails> getUserServicesDetails(@Param("myId") Long myId);
	
	
	@Modifying
	@Query("DELETE FROM ServiceDetails where id=:itemId")
	void deleteServiceDetailsItem(@Param("itemId") Long itemId);
	
	@Modifying
	@Query("DELETE FROM ServiceDetails sd where sd.user.id=:myId and sd.serviceType=:serviceType and sd NOT IN :services")
	void removeUnusedServicesDetailsItems(@Param("myId") Long myId, @Param("services") Set<ServiceDetails> services, @Param("serviceType") String serviceType );
	
	@Query("SELECT sd FROM ServiceDetails sd WHERE sd.user.id=:myId and sd.serviceType=:serviceType")
	public Set<ServiceDetails> getUserServicesDetailsByServiceType(@Param("myId") Long myId, @Param("serviceType") String serviceType);
	
	@Modifying
	@Query("DELETE FROM ServiceDetails WHERE user.id=:myId AND serviceType=:serviceType")
	public void deleteServicesDetailsByServiceType(@Param("myId") Long myId, @Param("serviceType") String serviceType);
}
