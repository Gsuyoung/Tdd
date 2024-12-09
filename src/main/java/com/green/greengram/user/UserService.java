package com.green.greengram.user;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;

    public int postSignUp(MultipartFile pic, UserSignUpReq p) {
        String savedPicName = (pic != null) ? myFileUtils.makeRandomFileName() : null;
        String hashedPassword = BCrypt.hashpw(p.getUpw(), BCrypt.gensalt());
        p.setUpw(hashedPassword);
        p.setPic(savedPicName);
        int result = mapper.insUser(p);

        if(pic == null) {
            return result;
        }

        long userId = p.getUserId();
        String middlePath = String.format("user/%d", userId);
        myFileUtils.makeFolders(middlePath);
        String filePath = String.format("%s/%s", middlePath, savedPicName);
        try {
        myFileUtils.transferTo(pic,filePath);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public UserSignInRes postSignIn(UserSignInReq p) {
        UserSignInRes res = mapper.selUserByUid(p.getUid());
        if(res == null) {
            res = new UserSignInRes();
            res.setMessage("아이디를 확인하세요.");
            return res;
        }
        if (!BCrypt.checkpw(p.getUpw(), res.getUpw())) {
            res = new UserSignInRes();
            res.setMessage("비밀번호를 확인하세요");
            return res;
        }
        res.setMessage("로그인 완료");
        return res;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p) {
        return mapper.selUserInfo(p);
    }

    public String patchUserPic(UserPicPatchReq p) {
        //1. 저장할 파일명(랜덤한 파일명) 생성한다. 이때, 확장자는 오리지널 파일명과 일치하게 한다.
        String savedPicName = p.getPic() != null ? myFileUtils.makeRandomFileName(p.getPic()) : null;

        //2. 폴더 만들기 ( 최초에 프로필 사진이 없었다면 폴더가 없기 떄문)
        String folderPath = String.format("user/%d", p.getSignedUserId());
        myFileUtils.makeFolders(folderPath);

        //3. 기존 파일 삭제(방법 3가지 [1] : 폴더를 지운다. [2] : select에서 기존 파일명을 얻어온다. [3] : 기존 파일명을 FE에서 받는다.)
       String deletePath = String.format("%s/user/%d", myFileUtils.getUploadPath(), p.getSignedUserId());
       myFileUtils.deleteFolder(deletePath, false);

        //4. DB에 튜플을 수정(Update)한다.
        p.setPicName(savedPicName);
        int result = mapper.updUserPic(p);

       if (p.getPic() == null) {
           return null;
       }

        //5. 원하는 위치에 지정할 파일명으로 파일을 이동(transferTo)한다.
        String filePath = String.format("user/%d/%s", p.getSignedUserId(), savedPicName );
        try {
            myFileUtils.transferTo(p.getPic(),filePath);
        } catch(IOException e) {
           e.printStackTrace();
        }

        return savedPicName;
    }
}
