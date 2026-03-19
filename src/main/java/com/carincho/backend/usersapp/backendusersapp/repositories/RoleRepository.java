package com.carincho.backend.usersapp.backendusersapp.repositories;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.carincho.backend.usersapp.backendusersapp.models.entities.Role;


public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
