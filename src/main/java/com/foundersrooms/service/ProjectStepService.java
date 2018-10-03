package com.foundersrooms.service;

import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Step;
import com.foundersrooms.domain.project.Task;

public interface ProjectStepService {
	Step saveProjectStep(Long projectId, String stepRawData);
	Task createStepTask(Long projectId,String parameter);
	Task assignTaskToProjectMember(Long taskId, Long projectMemberId);
	void removeStepTask(Long taskId);
	User removeProjectMember(Long projectMemberId);
	Task updateStepTask(Long taskId,String taskRawData);
	void removeMember(Long contactId, Long userConnectedId);
}
