package com.consort.database.entities;

import java.util.List;

public class Person {
    private String name;

    private List<String> roles;

    private String email;

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
