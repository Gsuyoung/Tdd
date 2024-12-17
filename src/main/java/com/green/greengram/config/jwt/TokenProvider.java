package com.green.greengram.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

//DI - 외부에서 직접 주소값을 넣어주는 것.
@Service
public class TokenProvider { //springcontainer가 직접 객체생성. 빈등록이 되어있으므로
    private final ObjectMapper objectMapper; //JackSon 라이브러리 (직렬화 - JwtUser에있는 데이터들을 json형태로 바꿔서 값을 넣어줄 수 있다.)
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenProvider(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
    }

    //JWT 토큰 생성
    //JwtUser Jwt 토큰을 만들때 사용. duration 만료시간을 설정
    public String generateToken(JwtUser jwtUser, Duration expiredAt) {
        Date now = new Date();
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));
    }

    private String makeToken(JwtUser jwtUser, Date expiry) {
        //JWT 암호화 하는 과정
        return Jwts.builder()
                .header().add("typ", "JWT")//(KEY, VALUE) -- header(헤더)
                         .add("alg", "HS256")
                .and()
                .issuer(jwtProperties.getIssuer()) //-- 내용
                .issuedAt(new Date())
                .expiration(expiry)
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(secretKey) //-- 서명
                .compact();
    }

    private String makeClaimByUserToString(JwtUser jwtUser) {
        //객체 자체를 JWT에 담고 싶어서 객체를 직렬화하는 과정
        //jwtUser에 담고있는 데이터를 JSON형태의 문자열로 변환
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String token) {
        try {
            //JWT 복호화 하는 과정
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //상속받고 있으므로 타입이 다르더라도 객체화할 수있다. (부모(Authentication)는 자식객체값을 담을 수 있다.)
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        return userDetails == null ?
                null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails getUserDetailsFromToken(String token) {
        //객체를 문자열로 바꿨고 다시 빼내와서 객체화 시키는 과정
        Claims claims = getClaims(token);
        String json = (String) claims.get("signedUser");
        JwtUser jwtUser = objectMapper.convertValue(json, JwtUser.class);
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}