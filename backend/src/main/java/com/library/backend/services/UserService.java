package com.library.backend.services;

public class UserService {
    public String greetUser(String name) {
        if (name == null || name.isBlank()) {
            return "Hello Guest";
        }
        return "Hello " + name;
    }
}