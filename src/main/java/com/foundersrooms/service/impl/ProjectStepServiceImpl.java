package com.foundersrooms.service.impl;



import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foundersrooms.domain.people.User;
import com.foundersrooms.domain.project.Project;
import com.foundersrooms.domain.project.ProjectMember;
import com.foundersrooms.domain.project.Step;
import com.foundersrooms.domain.project.Task;
import com.foundersrooms.repository.ProjectMemberRepository;
import com.foundersrooms.repository.ProjectRepository;
import com.foundersrooms.repository.StepRepository;
import com.foundersrooms.repository.TaskRepository;
import com.foundersrooms.repository.UserRepository;
import com.foundersrooms.service.ProjectStepService;
import com.google.gson.Gson;

@Service
public class ProjectStepServiceImpl implements ProjectStepService {

	@Autowired
	private StepRepository stepRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectMemberRepository projectMemberRepository;

	@Autowired
	private UserRepository userRepository;

	Gson gson = new Gson();

	private ProjectMember getProjectMemberReference(User user, Project project) {
		Set<ProjectMember> pms = projectMemberRepository.findByUserNameAndProjectName(user.getUsername(),
				project.getName());
		if (pms.size() == 0) {
			ProjectMember pm = new ProjectMember();
			pm.setMember(user);
			pm.setProject(project);
			pm = projectMemberRepository.save(pm);
			return pm;
		}
		return (ProjectMember) pms.toArray()[0];
	}

	private ProjectMember getProjectMemberByUserReference(User user, Project project) {
		Set<ProjectMember> pms = projectMemberRepository.findByUserNameAndProjectName(user.getUsername(),
				project.getName());
		if (pms.size() == 0) {
			ProjectMember pm = new ProjectMember();
			pm.setMember(user);
			pm.setProject(project);
			pm = projectMemberRepository.save(pm);
			return pm;
		}
		return (ProjectMember) pms.toArray()[0];
	}

	@Override
	public Task createStepTask(Long projectId, String parameter) {
		ProjectMember pm;
		Project project = projectRepository.findOne(projectId);
		Task task = gson.fromJson(parameter, Task.class);
		Step projectStep = task.getProjectStep();
		Step stepDatabaseItem = stepRepository.findOne(projectStep.getId());		
		task.setProjectStep(stepDatabaseItem);
		if (task.getUserReference() != null) {
			pm = getProjectMemberByUserReference(task.getUserReference(), project);
			task.setAssignedTo(pm);
		}		
		task = taskRepository.save(task);
		stepDatabaseItem.getStepTasks().add(task);
		stepDatabaseItem=stepRepository.save(stepDatabaseItem);
		updateStepTaskRatio(stepDatabaseItem);
		project=projectRepository.save(project);
		return task;
	}

	@Override
	public Task assignTaskToProjectMember(Long taskId, Long projectMemberId) {
		// TODO Auto-generated method stub
		ProjectMember pm = projectMemberRepository.findOne(projectMemberId);
		Task task = taskRepository.findOne(taskId);
		task.setAssignedTo(pm);
		task = taskRepository.save(task);
		pm.getProjectMemberTasks().add(task);
		pm = projectMemberRepository.save(pm);
		return task;
	}

	@Override
	public void removeStepTask(Long taskId) {
		Task task=taskRepository.findOne(taskId);
		Step step=task.getProjectStep();
		step.getStepTasks().remove(task);
		taskRepository.delete(task);
		step=stepRepository.save(step);
		updateStepTaskRatio(step);
	}

	@Override
	public User removeProjectMember(Long projectMemberId) {
		ProjectMember pm = projectMemberRepository.findOne(projectMemberId);
		User removedUser = userRepository.findOne(pm.getMember().getId());
		projectMemberRepository.delete(pm);
		return removedUser;
	}

	private int countCompletedTask(Set<Task> tasks) {
		int tmp = 0;
		for (Task item : tasks) {
			if (item.isCompleted())
				tmp = tmp + 1;
		}
		return tmp;
	}

	private float calculateTaskRatio(int assignedTaskNumber, int completedTaskNumber) {
		if (assignedTaskNumber != 0)
			return completedTaskNumber / assignedTaskNumber;
		return 0f;
	}

	@Override
	public Step saveProjectStep(Long projectId, String stepRawData) {
		Project project = projectRepository.findOne(projectId);
		Step step = gson.fromJson(stepRawData, Step.class);
		if (step.getId() != null) {
			Step databaseInstance = stepRepository.findOne(step.getId());
			databaseInstance.setDescription(step.getDescription());
			databaseInstance.setStepName(step.getStepName());
			int completedTasks = countCompletedTask(step.getStepTasks());
			databaseInstance.setCompletedTaskNumber(completedTasks);
			databaseInstance.setAssignedTaskNumber(step.getStepTasks().size());
			databaseInstance.setTaskRatio(step.getTaskRatio());
			// databaseInstance.setTaskRatio(calculateTaskRatio(step.getStepTasks().size(),completedTasks));
			if (completedTasks == step.getStepTasks().size() && step.getStepTasks().size() != 0)
				databaseInstance.setCompleted(true);
			else
				databaseInstance.setCompleted(false);
			step = stepRepository.save(databaseInstance);
		} else {
			step.setProjectReference(project);
			step = stepRepository.save(step);
			project.getProjectSteps().add(step);
			projectRepository.save(project);
		}
		return step;
	}

	private void removeTaskFromProjectMemberTasks(Task task) {
		if (task.getAssignedTo() != null) {
			ProjectMember pm = projectMemberRepository.findOne(task.getAssignedTo().getId());
			pm.getProjectMemberTasks().remove(task);
			task.setAssignedTo(null);
			projectMemberRepository.save(pm);
		}
	}

	private void updateProjectMemberIfDifferent(Task requestItem, Task dbItem,ProjectMember currentProjectMember) {
		if(dbItem.getUserReference()!=null) {
			if(dbItem.getAssignedTo().getId() != currentProjectMember.getId()) {
				ProjectMember pm = dbItem.getAssignedTo();
				pm.getProjectMemberTasks().remove(dbItem);
				projectMemberRepository.save(pm);
			}
		}
	}

	private Step updateStepTaskRatio(Step step) {
		step = stepRepository.findOne(step.getId());
		Set<Task> tasks = step.getStepTasks();
		float value = 0f, totalTask = step.getStepTasks().size();		
		for (Task task : tasks)
			if (task.isCompleted())
				value = value + 1;
		if (totalTask > 0)
			step.setTaskRatio(value / totalTask);
		step.setAssignedTaskNumber((int) totalTask);
		step.setCompletedTaskNumber((int) value);
		if (totalTask == value && totalTask > 0)
			step.setCompleted(true);
		else
			step.setCompleted(false);
		stepRepository.save(step);
		return step;
	}

	@Transactional
	private void removeProjectMemberIfNoMoreAssignment(Long pmId) {		
		Task task=taskRepository.checkIfAnyTaskStillAssignedToUser(pmId);
		ProjectMember pm=projectMemberRepository.findOne(pmId);
		Project project=pm.getProject();
		if(task==null) 	
			if(!project.getCreator().getId().equals(pm.getMember().getId()))
				projectMemberRepository.deleteProjectMember(pmId);				
	}
	
	@Override
	@Transactional
	public Task updateStepTask(Long projectId, String taskRawData) {
		Project project = projectRepository.findOne(projectId);
		Long currentAssigneeId=null;
		Task task = gson.fromJson(taskRawData, Task.class);
		Task dbTask = taskRepository.findOne(task.getId());
		if(dbTask.getAssignedTo()!=null) 
			currentAssigneeId=dbTask.getAssignedTo().getId();
		
		dbTask.setCompleted(task.isCompleted());
		dbTask.setTaskName(task.getTaskName());
		if (task.getUserReference() != null) {
			ProjectMember pm = getProjectMemberByUserReference(task.getUserReference(), project);
			updateProjectMemberIfDifferent(task, dbTask,pm);
			dbTask.setAssignedTo(pm);
			pm = projectMemberRepository.save(pm);	
		}
		else
			removeTaskFromProjectMemberTasks(dbTask);
		task = taskRepository.save(dbTask);
		updateStepTaskRatio(task.getProjectStep());
		project=projectRepository.save(project);
		if(currentAssigneeId!=null)
			removeProjectMemberIfNoMoreAssignment(currentAssigneeId);
		return task;
	}

	@Override
	@Transactional
	public void removeMember(Long contactId,Long userConnectedId) {
		// TODO Auto-generated method stub
		taskRepository.removeProjectMemberAssignment(userConnectedId,contactId);
		//stepRepository.deleteProjectMemberFromUserProject(userConnectedId);
	}

}
