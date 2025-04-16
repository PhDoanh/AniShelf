package com.library.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    @Test
    public void testGreetUser() {
        UserService service = new UserService();
        assertEquals("Hello John", service.greetUser("John"));
    }
}