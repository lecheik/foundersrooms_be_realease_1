package com.foundersrooms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.step.ProjectStepDeliverable;

public interface ProjectStepDeliverableRepository extends CrudRepository<ProjectStepDeliverable, Long> {

	
	@Query("SELECT psd FROM ProjectStepDeliverable psd WHERE psd.worflowStepReference.id=:stepId AND psd.labelSlug=:deliverableLabel")
	ProjectStepDeliverable getPSDByProjectStepAndDeliverableLabel(@Param("stepId") Long projectStepId, @Param("deliverableLabel") String deliverableLabel);
	
	@Query("SELECT psd FROM ProjectStepDeliverable psd WHERE psd.worflowStepReference.id=:projectStepId AND psd.deliverableNum=:deliverableRef")
	ProjectStepDeliverable getPSDByProjectStepAndDeliverableRef(@Param("projectStepId") Long projectStepId, @Param("deliverableRef") int deliverableRef);
}
