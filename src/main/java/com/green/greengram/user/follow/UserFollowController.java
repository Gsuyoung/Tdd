package com.green.greengram.user.follow;

import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.user.follow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

@Slf4j
@RestController
@RequestMapping("user/follow")
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService service;

    //팔로우 신청
    //@requestBody, 요청을 보내는 자가 body에 json형태의 데이터를 담아서 보낸다는 뜻.
    @PostMapping
    public ResultResponse<Integer> postUserFollow(@RequestBody UserFollowReq p) {
        log.info("UserFollowController > postUserFollow > p: {}", p);
        Integer result = service.postUserFollow(p);
        return ResultResponse.<Integer>builder().resultMsg("팔로우 완료").resultData(result).build();
    }



    //팔로우 취소
    //요청을 보내는 자가 쿼리스트링방식으로 데이터를 보낸다.
    @DeleteMapping
    public ResultResponse<Integer> deleteUserFollow(@ParameterObject @ModelAttribute UserFollowReq p) {
        Integer res = service.deleteUserFollow(p);
        return ResultResponse.<Integer>builder().resultMsg("팔로우 삭제").resultData(res).build();
    }
}
