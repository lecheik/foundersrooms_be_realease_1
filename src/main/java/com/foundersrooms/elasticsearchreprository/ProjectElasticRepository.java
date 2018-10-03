package com.foundersrooms.elasticsearchreprository;

import java.util.Set;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.foundersrooms.domain.project.Project;

public interface ProjectElasticRepository extends ElasticsearchRepository<Project, Long> {
	Set<Project> findByCreator_Id(Long userId);
}
