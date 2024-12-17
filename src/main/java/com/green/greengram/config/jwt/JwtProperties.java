package com.green.greengram.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("jwt") //yaml에 있는 jwt아래에 있는 값들을 setter를 통해서 담겠다.
public class JwtProperties {
    private String issuer; //yaml에 있는 issuer이름과 맞춰주면된다.
    private String secretKey; //'-'는 대문자로 표현
}
