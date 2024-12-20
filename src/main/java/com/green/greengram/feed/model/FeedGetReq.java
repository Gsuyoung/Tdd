package com.green.greengram.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.green.greengram.common.model.Paging;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.BindParam;

@Slf4j
@Getter
@ToString(callSuper = true) //부모가 가지고 있는 값도 찍힐수있도록 하기위함 --> lombok기능
public class FeedGetReq extends Paging {
    @JsonIgnore
    private long signedUserId; //Schema name을 설정하지않으면 swagger상 <-- key 값이된다.

    @Positive //1이상 정수이어야 한다.
    @Schema(title = "프로필 유저 PK", name = "profile_user_id", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long profileUserId;

    //@ConstructorProperties({"page", "size", "signed_user_id"}) --> 전체적으로 바꿀때 사용(GET,DELETE방식일때만 사용)
    //쿼리스트링방식만 신경쓰면된다.(쿼리스트링방식(처리속도가 젤 빠르다) --> GET, DELETE방식일때 사용)
    //@BindParam --> 개별적으로 이름을 바꿀때 사용(언더바 없앨때사용)
    //@SignedUserId 를 받은 이유 : 좋아요 처리하기위해서
    public FeedGetReq(Integer page, Integer size, @BindParam("profile_user_id") Long profileUserId) {
        super(page, size);
        this.profileUserId = profileUserId;
        log.info("page: {}, size: {}, profileUserId: {}", page, size, profileUserId);
    }

    public void setSignedUserId(long signedUserId) {
        this.signedUserId = signedUserId;
    }
}