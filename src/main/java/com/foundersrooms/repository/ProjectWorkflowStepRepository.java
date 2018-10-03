package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.step.ProjectWorkflowStep;

public interface ProjectWorkflowStepRepository extends CrudRepository<ProjectWorkflowStep, Long> {
	
	@Query("SELECT pws FROM ProjectWorkflowStep pws WHERE pws.projectReference.id=:projectId AND pws.stepNum=:stepNum")
	Set<ProjectWorkflowStep> getWorkflowStepByStepNum(@Param("stepNum") int stepNum, @Param("projectId") Long projectId);
	
}
