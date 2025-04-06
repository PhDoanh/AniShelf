package com.library.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.library.backend.UserService;

public class UserServiceTest {

    @Test
    public void testGreetUser() {
        UserService service = new UserService();
        assertEquals("Hello John", service.greetUser("John"));
    }
}