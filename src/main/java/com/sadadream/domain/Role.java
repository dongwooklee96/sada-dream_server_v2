package com.sadadream.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long userId;

    @Getter
    private String role;

    public Role(String role) {
        this.role = role;
    }

    public Role(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}
