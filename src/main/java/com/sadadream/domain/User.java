package com.sadadream.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.sadadream.dto.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    private Long id;

    private String email;

    private String name;

    @Builder.Default
    private String password = "";

    private String phoneNumber;

    private String address;



    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    @Builder.Default
    private boolean deleted = false;

    public void changeWith(User source) {
        name = source.name;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void destroy() {
        this.deleted = true;
    }

    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !this.deleted && passwordEncoder.matches(password, this.password);
    }
}
