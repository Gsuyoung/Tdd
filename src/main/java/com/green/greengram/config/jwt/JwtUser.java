package com.green.greengram.config.jwt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JwtUser {
    private long signedUserId; //로그인한 유저 ID
    private List<String> roles; //로그인 할 때 인가(권한)처리 용도로 사용
}