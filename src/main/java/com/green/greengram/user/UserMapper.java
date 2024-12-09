package com.green.greengram.user;

import com.green.greengram.user.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insUser(UserSignUpReq p);
    UserSignInRes selUserByUid(String p);
    UserInfoGetRes selUserInfo(UserInfoGetReq p);
    int updUserPic(UserPicPatchReq p);
}
