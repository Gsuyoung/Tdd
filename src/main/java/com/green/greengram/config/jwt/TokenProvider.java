package com.green.greengram.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.exception.CustomException;
import com.green.greengram.common.exception.UserErrorCode;
import com.green.greengram.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.core.util.Json;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

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
        Date now = new Date(); //기본 생성자
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis())); // 오버라이딩된 생성자
    }

    private String makeToken(JwtUser jwtUser, Date expiry) {
        //JWT 암호화 하는 과정 (문자열)
        return Jwts.builder()
                .header().type("JWT")//(KEY, VALUE) -- header(헤더)
                .and()
                .issuer(jwtProperties.getIssuer()) //-- 내용
                .issuedAt(new Date())
                .expiration(expiry) //만료시간
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(secretKey) //-- 서명
                .compact(); //return 타입은 String. private String으로 선언하였고 그값을 리턴할 수 있으므로
                            //String 타입이 된다.
    }

    private String makeClaimByUserToString(JwtUser jwtUser) {
        //객체 자체를 JWT에 담고 싶어서 객체를 직렬화하는 과정(객체를 String으로 바꾸는 작업)
        //jwtUser에 담고있는 데이터를 JSON형태의 문자열로 변환
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /* public boolean validToken(String token) {
        try {
            //JWT 복호화 하는 과정
            getClaims(token);
        } catch (Exception e) {
            throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
        }
        return true;
    } */

    //Spring Security에서 인증 처리를 해주어야 한다. 그때 Authentication 객체가 필요.
    //상속받고 있으므로 타입이 다르더라도 객체화할 수있다. (부모(Authentication)는 자식객체값을 담을 수 있다.)
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        return userDetails == null ?
                null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public JwtUser getJwtUserFromToken(String token) {
        //객체를 문자열로 바꿨고 다시 빼내와서 객체화 시키는 과정
        Claims claims = getClaims(token);
        String json = (String)claims.get("signedUser");
        JwtUser jwtUser = null;
        try {
            jwtUser = objectMapper.readValue(json, JwtUser.class);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jwtUser;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        JwtUser jwtUser = getJwtUserFromToken(token);
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}