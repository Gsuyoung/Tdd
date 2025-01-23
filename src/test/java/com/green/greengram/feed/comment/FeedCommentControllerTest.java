package com.green.greengram.feed.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.comment.model.FeedCommentPostReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//controller만 test하겠다(slice 테스트)
@WebMvcTest(
        controllers = FeedCommentController.class
        , excludeAutoConfiguration = SecurityAutoConfiguration.class
)

//@Import({MyFileUtils.class}) --> 가짜 객체가 아닌 이것 만큼은 진짜 객체를 만들고싶을때 사용
class FeedCommentControllerTest {

    @Autowired ObjectMapper objectMapper; //JSON사용 (@Autowired - DI)
    @Autowired MockMvc mockMvc; //요청(보내고) - 응답(받기) 처리
    //@MockBean과 @Mock의 차이점 : @MockBean은 spring context와 관련이 있다.(DI를 받을 수 있다.)
    @MockBean FeedCommentService FeedCommentService; //가짜 객체를 만들고 빈등록한다.

    final long feedId_2 = 2L;
    final long feedCommentId_3 = 3L;
    final long writerUserId_4 = 4L;
    final String BASE_URL = "/api/feed/comment";

    final int startIdx = 1;
    final int SIZE = 20;

    @Test
    @DisplayName("피드 댓글 등록 테스트")
    void postFeedComment() throws Exception {
        FeedCommentPostReq givenParam = new FeedCommentPostReq();
        givenParam.setFeedId(feedId_2);
        givenParam.setComment("코멘트");

        given(feedCommentService.postFeedComment(givenParam)).willReturn(feedCommentId_3); //3L이 리턴될거야

        String paramJson = objectMapper.writeValueAsString(givenParam);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(paramJson));

        ResultResponse res = ResultResponse.<Long>builder()
                .resultMsg("댓글 등록 완료")
                .resultData(feedCommentId_3)
                .build();

        String expectedResJson = objectMapper.writeValueAsString(res);
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));

        verify(feedCommentService).postFeedComment(givenParam);
    }

    @Autowired
    private FeedCommentService feedCommentService;

    @Test
    void getFeedComment() throws Exception {


        //key1 = value1 & key2 = value2 & key3 = value3 (쿼리스트링) --> get방식, delete방식
        //feed_id = 2 & start_idx = 1
        //feed_id = 2 & start_idx = 1 & size = 20


        FeedCommentGetReq givenParam = new FeedCommentGetReq(feedId_2, startIdx, SIZE);

        FeedCommentDto feedCommentDto = new FeedCommentDto();
        feedCommentDto.setFeedId(feedId_2);
        feedCommentDto.setFeedCommentId(feedCommentId_3);
        feedCommentDto.setComment("코멘트");
        feedCommentDto.setWriterUserId(writerUserId_4);
        feedCommentDto.setComment("작성자");
        feedCommentDto.setWriterPic("profile.jpg");

        FeedCommentGetRes expectdResult = new FeedCommentGetRes();
        expectdResult.setMoreComment(false);
       //expectdResult.setCommentList(new ArrayList<>(1));
        expectdResult.setCommentList(List.of(feedCommentDto));

        //service.getFeedComment에 임무부여
        given(feedCommentService.getFeedComment(givenParam)).willReturn(expectdResult);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL).queryParams(getParameter(givenParam)));

        String expectedResJson = getExpectedResJson(expectdResult);
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));

        verify(feedCommentService).getFeedComment(givenParam);
    }

    private MultiValueMap<String, String> getParameter(FeedCommentGetReq givenParam) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(1); //<-- 쿼리스트링 방식으로 만들어주는 객체
        queryParams.add("feed_id", String.valueOf(givenParam.getFeedId()));//key값, value값
        queryParams.add("start_idx", String.valueOf(startIdx));//key값, value값
        queryParams.add("size", String.valueOf(SIZE));//key값, value값
        return queryParams;
    }

    private String getExpectedResJson(FeedCommentGetRes result) throws Exception {
        ResultResponse expectedRes = ResultResponse.<FeedCommentGetRes>builder()
                .resultMsg(String.format("%d rows", result.getCommentList().size()))
                .resultData(result)
                .build();
        return objectMapper.writeValueAsString(expectedRes);
    }
}