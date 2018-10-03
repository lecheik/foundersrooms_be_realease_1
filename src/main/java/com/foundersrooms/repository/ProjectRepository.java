package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;

public interface ProjectRepository extends CrudRepository<Project, Long>, JpaSpecificationExecutor<Project> {
	
	@Query("SELECT DISTINCT p FROM Project p, ProjectMember pm WHERE pm.project.id=p.id and (p.creator.id=:myId OR pm.member.id=:myId) and archived=false and pm.activeMember=true ORDER BY p.name ASC")
	Set<Project> getMyActiveProjects(@Param("myId") Long myId);
	
	@Query("SELECT DISTINCT p FROM Project p, ProjectMember pm WHERE pm.project.id=p.id and (p.creator.id=:myId OR pm.member.id=:myId) and archived=true and pm.activeMember=true ORDER BY p.name ASC")
	Set<Project> getMyArchiveProjects(@Param("myId") Long myId);	
	
	Project findByName(String name);
	
	Project findByNameSlug(String nameSlug);
	
	@Query("SELECT p.members FROM Project p WHERE p.id=:projectId")
	Set<ProjectMember> getProjectMembersFromProjectId(@Param("projectId") Long projectId);
	
	Project findByNameAllIgnoreCase(String name);

}
