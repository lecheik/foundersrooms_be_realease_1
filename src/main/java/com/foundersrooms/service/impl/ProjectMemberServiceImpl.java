package com.foundersrooms.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.repository.ProjectMemberRepository;
import com.foundersrooms.service.ProjectMemberService;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

	@Autowired
	ProjectMemberRepository projectMemberRepository;
	
	@Override
	public ProjectMember saveProjectMember(ProjectMember pm) {
		// TODO Auto-generated method stub
		Set<ProjectMember> result=projectMemberRepository.findByUserNameAndProjectName(pm.getMember().getUsername(), pm.getProject().getName());
		if(result.size()==0) {
			projectMemberRepository.save(pm);
			return pm;
		}
		return (ProjectMember)result.toArray()[0];
	}

}
