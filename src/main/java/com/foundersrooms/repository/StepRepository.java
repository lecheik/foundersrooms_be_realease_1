package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.Step;

public interface StepRepository extends CrudRepository<Step, Long> {

	@Modifying
	@Query("DELETE FROM ProjectMember pm WHERE pm.project.id IN (SELECT p.id FROM Project p WHERE p.creator.id=:creatorId)")
	public void deleteProjectMemberFromUserProject(@Param("creatorId") Long creatorId);
	
}
