package com.ivansaprykin.testtasks.bostongene.springuserservice.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class User {

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordEncoded;

    public User() {
    }

    public User(SimplifiedUser simplifiedUser, String  passwordEncoded) {
        this.firstName = simplifiedUser.getFirstName();
        this.lastName = simplifiedUser.getLastName();
        this.birthDate = simplifiedUser.getBirthDate();
        this.email = simplifiedUser.getEmail();
        this.passwordEncoded = passwordEncoded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordEncoded() {
        return passwordEncoded;
    }

    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }
}
