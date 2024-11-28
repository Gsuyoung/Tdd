package com.green.greengramver.user;

import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.user.model.UserSignInReq;
import com.green.greengramver.user.model.UserSignInRes;
import com.green.greengramver.user.model.UserSignUpReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}