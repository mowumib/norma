package com.hotelbooking.norma.entity;



import com.hotelbooking.norma.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleEnum roleName;


    public Role(RoleEnum name){
        this.roleName = name;
    }

    public RoleEnum getRoleName() {
        return roleName;
    }
    public Role setRoleName(RoleEnum roleName) {
        this.roleName = roleName;
        return this;
    }

    @Override
    public String toString() {
        return roleName.name();
    }
}
