package com.green.greengram.feed.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.feed.like.model.FeedLikeReq;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
public class FeedLikeTestCommon {
    private final ObjectMapper objectMapper;

    public MultiValueMap<String, String> getParameter(long feedId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(1); //<-- 쿼리스트링 방식으로 만들어주는 객체
        queryParams.add("feedId", String.valueOf(feedId)); //key값, value값
        return queryParams;
    }

    public FeedLikeReq getGivenParam(long feedId) {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(feedId);
        return givenParam;
    }

    public String getExpectedResJson(int result) throws Exception {
        ResultResponse expectedRes = ResultResponse.<Integer>builder()
                .resultMsg(result == 0 ? "좋아요 취소" : "좋아요 등록")
                .resultData(result)
                .build();
        return objectMapper.writeValueAsString(expectedRes);
    }
}
