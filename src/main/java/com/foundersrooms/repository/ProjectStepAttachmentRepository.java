package com.foundersrooms.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.step.ProjectStepAttachment;

public interface ProjectStepAttachmentRepository extends CrudRepository<ProjectStepAttachment, Long> {
	@Modifying
	@Query("DELETE FROM ProjectStepAttachment psa WHERE psa.id=:psaId")
	void deleteProjectStepAttachment(@Param("psaId") Long psaId);
}
