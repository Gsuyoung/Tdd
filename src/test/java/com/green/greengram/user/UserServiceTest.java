package com.green.greengram.user;

import com.green.greengram.config.jwt.JwtUser;
import com.green.greengram.config.jwt.TokenProvider;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private TokenProvider tokenProvider;

    @Test
    public void generateToken() {
        JwtUser jwtUser = new JwtUser();
        jwtUser.setSignedUserId(10);

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        jwtUser.setRoles(roles);

        //When (실행단계)
        String token = tokenProvider.generateToken(jwtUser, Duration.ofMinutes(20));

        //Then (검증단계)
        assertNotNull(token);
    }
}