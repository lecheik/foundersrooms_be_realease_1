package com.foundersrooms.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.step.ProjectStepNote;

public interface ProjectStepNoteRepository extends CrudRepository<ProjectStepNote, Long> {

	@Modifying
	@Query("DELETE FROM ProjectStepNote psn WHERE psn.id=:psnId")
	void deleteProjectStepNote(@Param("psnId") Long psnId);
}
