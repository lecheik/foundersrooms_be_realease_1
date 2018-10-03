package com.foundersrooms.repository;

import org.springframework.data.repository.CrudRepository;

import com.foundersrooms.domain.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long>{

}
