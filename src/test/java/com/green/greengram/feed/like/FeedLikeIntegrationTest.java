package com.green.greengram.feed.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.WithAuthUser;
import com.green.greengram.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //test가 여러개라도 하나만 실행 가능
@WithAuthUser
public class FeedLikeIntegrationTest extends BaseIntegrationTest {
    FeedLikeTestCommon common;
    final String BASE_URL = "/api/feed/like";

    @BeforeAll
    void setUp() {
        common = new FeedLikeTestCommon(objectMapper);
    }

    @Test
    @DisplayName("좋아요 등록")
    @Rollback(false)
    void feedLikeReg() throws Exception {
        final int regSuccessResult = 1;
        final long feedIdNoExisted = 5L;
        feedLikeToggle(regSuccessResult, feedIdNoExisted);
    }

    @Test
    @DisplayName("좋아요 취소")
    void feedLikeCancel() throws Exception {
        final int cancelSuccessResult = 0;
        final long feedIdExisted = 2L;
        feedLikeToggle(cancelSuccessResult, feedIdExisted);
    }

    void feedLikeToggle(final int result, final long feedId) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(BASE_URL).queryParams(common.getParameter(feedId)));

        String expectedResJson = common.getExpectedResJson(result);
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));
    }

}
