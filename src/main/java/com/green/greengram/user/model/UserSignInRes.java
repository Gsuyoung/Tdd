package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(title = "로그인 응답")
public class UserSignInRes {
    private long userId;
    private String nickName;
    private String pic;
    private String accessToken;

    @JsonIgnore //swagger 문서상 표시도 안되지만, 응답 때 빼는 역할도 한다.
    private String upw;
    @JsonIgnore
    private String message;
}
