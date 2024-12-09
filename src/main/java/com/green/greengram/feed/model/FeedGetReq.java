package com.green.greengram.feed.model;

import com.green.greengram.common.model.Paging;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.BindParam;

@Slf4j
@Getter
@ToString(callSuper = true) //부모가 가지고 있는 값도 찍힐수있도록 하기위함 --> lombok기능
public class FeedGetReq extends Paging {
    @Schema(title = "로그인 유저 PK", name = "signed_user_id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long signedUserId; //Schema name을 설정하지않으면 swagger상 <-- key 값이된다.

    @Schema(title = "프로필 유저 PK", name = "profile_user_id", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long profileUserId;

    //@ConstructorProperties({"page", "size", "signed_user_id"}) --> 전체적으로 바꿀때 사용(GET,DELETE방식일때만 사용)
    //쿼리스트링방식만 신경쓰면된다.(쿼리스트링방식(처리속도가 젤 빠르다) --> GET, DELETE방식일때 사용)
    //@BindParam --> 개별적으로 이름을 바꿀때 사용(언더바 없앨때사용)
    //@SignedUserId 를 받은 이유 : 좋아요 처리하기위해서
    public FeedGetReq(Integer page, Integer size, @BindParam("signed_user_id") long signedUserId,
                                                  @BindParam("profile_user_id") Long profileUserId) {
        super(page, size);
        this.signedUserId = signedUserId;
        this.profileUserId = profileUserId;
        log.info("page: {}, size: {}, userId: {}, profileUserId: {}", page, size, signedUserId, profileUserId);
    }
}