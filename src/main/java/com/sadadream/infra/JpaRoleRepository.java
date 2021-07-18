package com.sadadream.infra;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sadadream.domain.Role;
import com.sadadream.domain.RoleRepository;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {
    List<Role> findAllByUserId(Long userId);
}
