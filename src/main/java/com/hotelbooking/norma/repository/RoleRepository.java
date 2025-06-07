package com.hotelbooking.norma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelbooking.norma.entity.Role;
import com.hotelbooking.norma.entity.UserRole;
import com.hotelbooking.norma.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long>{
    
    Boolean existsByRoleName(String email);

    @Query(value = "SELECT rl.role_name, ur.role_id as role_id FROM user_role_assignments ur INNER JOIN role rl ON rl.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    List<UserRole> getUserRoles(String userId);

    @Query("SELECT r FROM Role r WHERE r.id = :roleId")
    Role getRoleNameById(@Param("roleId") Long roleId);

    Optional<Role> findByRoleName(RoleEnum roleName);
    
}
