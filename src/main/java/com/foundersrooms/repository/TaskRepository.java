package com.foundersrooms.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {
	
	@Query("SELECT t FROM Task t WHERE t.assignedTo.id=:projectMemberId")
	public Task checkIfAnyTaskStillAssignedToUser(@Param("projectMemberId") Long projectMemberId);
	
	@Modifying
	//@Query(nativeQuery = true,value="UPDATE Task t set t.assignedTo = NULL WHERE t.assignedTo.member.id=:userId")
	@Query(nativeQuery = true,value="UPDATE Task t set t.assigned_to = null WHERE t.assigned_to IN (SELECT pm.id FROM"
			+ "Project p inner join Step s on s.project_id=p.id inner join task t on t.project_step_id=s.id "
			+ "inner join project_member pm on pm.project_id=p.id WHERE p.creator_id=:creatorId AND "
			+ " pm.member_id=:contactId")
	public void removeProjectMemberAssignment(@Param("creatorId") Long creatorId, @Param("contactId") Long contactId);
}
