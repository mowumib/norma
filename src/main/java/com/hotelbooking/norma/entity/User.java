package com.hotelbooking.norma.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    private Long id;

    @Column(name = "user_code", unique = true, nullable = false)
    private String userCode;
    
    @Column(name = "name")
    private String name;

    @Column(name= "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name= "password", nullable = false)
    private String password;

    @Column(name= "is_validated", nullable = false)
    private boolean isValidated = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role_assignments", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name())) // e.g. "ROLE_USER"
            .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
