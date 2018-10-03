package com.foundersrooms.service;

import java.util.List;
import java.util.Set;

import com.foundersrooms.domain.people.Creator;
import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;

public interface ProjectService {
	Object getMyProjects(Long myId);
	
	Project getProjectById(Long projectId);
	
	Project findProjectByNameSlug(String param);
	
	Project findElasticProjectById(Long projectId);

	Project saveProject(Project project);
	
	Project saveElasticProject(Project project);
	
	Project createProject(Project project, Set<ProjectMember> projectMembers) throws Exception;
		
	Set<ProjectMember> getProjectMembersFromProjectId(Long projectId);
	
	Project updateProjectState(Long projectId,boolean projectState);
	
	void deleteProject(Long id);
	
	Project saveUpdatedProject(Project requestItem);
	
	ProjectMember addProjectMember(Long projectId, Long userId);	
	
	Set<ProjectMember> addProjectMembers(Long projectId, List<Long> userIds);
	
	Set<ProjectMember> getActiveProjectMembers(Long projectId);
	
	void updateProjectCreatorDescription(Creator user);
	
}
