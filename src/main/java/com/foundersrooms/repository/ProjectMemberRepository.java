package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.ProjectMember;

public interface ProjectMemberRepository extends CrudRepository<ProjectMember, Long> {

	@Query("SELECT pm FROM ProjectMember pm WHERE pm.member.username=:userName AND pm.project.name=:projectName")
	public Set<ProjectMember> findByUserNameAndProjectName(@Param("userName")String userName, @Param("projectName")String projectName);
	
	@Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id=:projectId")
	public Set<ProjectMember> findPorjectMembersByProjectId(@Param("projectId") Long projectId);
	
	@Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id=:projectId AND pm.activeMember=true")
	public Set<ProjectMember> findActiveProjectMemberByProject(@Param("projectId") Long projectId);
	
	@Modifying
	@Query("DELETE FROM ProjectMember pm WHERE pm.id=:pmId")
	void deleteProjectMember(@Param("pmId") Long pmId);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ProjectMember pm SET pm.activeMember=false where pm.project.id=:projectId")
	void deactivateAllProjectMembers(@Param("projectId") Long projectId);
	
	@Modifying
	//@Query("UPDATE ProjectMember pm, User usr, Project pr SET pm.activeMember=false WHERE pm.project.id=pr.id and usr.id=pm.member.id and pr.creator.id=:myId and usr.id=:contactId")
	//@Query("UPDATE ProjectMember pm SET pm.activeMember=false WHERE pm.project.creator.id=:myId AND pm.member.id=:contactId")
	@Query("UPDATE ProjectMember pm SET pm.activeMember=false WHERE pm.id IN (SELECT pmb.id FROM ProjectMember pmb, Project pr, User usr WHERE pmb.project=pr and usr=pmb.member and pr.creator.id=:myId and usr.id=:contactId)")
	void disableOldContactFromMyProjects(@Param("myId") Long myId, @Param("contactId") Long contactId);
	
	@Modifying
	@Query("UPDATE ProjectMember pm SET pm.activeMember=false WHERE pm.id IN :pmIds")
	void disableOldContactFromMyProjects(@Param("pmIds") Set<Long> pmIds);
	
	@Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id=:projectId AND pm.member.id=:userId")
	ProjectMember getProjectMemberFromUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
	
	@Query("SELECT pm.id FROM ProjectMember pm, Project pr WHERE pm.member.id=:userId AND pr.creator.id=:myId")
	Set<Long> findProjectMemberIdsRelatedToUserId(@Param("userId") Long userId, @Param("myId") Long myId);
}
