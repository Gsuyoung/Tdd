package com.green.greengram.config.jwt;

import com.green.greengram.config.security.MyUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //통합 테스트때 사용
class TokenProviderTest {
    //테스트는 생성자를 이용한 DI가 불가능
    //DI방법은 필드, setter메소드, 생성자
    //테스트 때는 필드 주입방식을 사용한다.

    @Autowired //생성자 없이도 DI를 가능하게 하는 애노테이션
    private TokenProvider tokenProvider;

    @Test
    public void generateToken() {
        //Given (준비단계)
        JwtUser jwtUser = new JwtUser();
        jwtUser.setSignedUserId(10);

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        jwtUser.setRoles(roles);

        //When (실행단계)
        String token = tokenProvider.generateToken(jwtUser, Duration.ofHours(3));

        //Then (검증단계)
        assertNotNull(token);

        System.out.println("token: " + token);
    }

    @Test
    void validToken() {
        //1분(이미 지남)
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMTQ2NCwiZXhwIjoxNzM0NDAxNTI0LCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.yTCXqeNwfoFNAqxPGToVIiFEYWwl-id8xr4F1F055EdOOtV-TyzldZ3ERHk_Ix7V\n";

        boolean result = tokenProvider.validToken(token);

        assertFalse(result);
    }

    @Test
    void getToken() {
        //3시간 짜리
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMzY0OCwiZXhwIjoxNzM0NDE0NDQ4LCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.UBHkL5EFWfOH0ITSEAd5aqL30JrQ8-F02ArEexIktvtdsRduGniBv7OgD_GuopTh";

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertNotNull(authentication);

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        JwtUser jwtUser = myUserDetails.getJwtUser();

        JwtUser expectedJwtUser = new JwtUser();
        expectedJwtUser.setSignedUserId(10);

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        expectedJwtUser.setRoles(roles);

        assertEquals(expectedJwtUser, jwtUser);
    }

}