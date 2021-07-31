package com.sadadream.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sadadream.dto.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Product> products;

    public void changeWith(User source) {
        this.name = source.getName();
        this.address = source.getAddress();
        this.phoneNumber = source.getPhoneNumber();
        this.gender = source.getGender();
        this.birthDate = source.getBirthDate();
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
