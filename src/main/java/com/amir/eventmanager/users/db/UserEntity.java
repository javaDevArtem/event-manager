package com.amir.eventmanager.users.db;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;
    @Column(name = "login")
    public String login;
    @Column(name = "age")
    public Integer age;
    @Column(name = "role")
    public String role;
    @Column(name = "passwordHash")
    public String passwordHash;

    public UserEntity() {
    }

    public UserEntity(Long id, String login, Integer age, String role, String passwordHash) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
