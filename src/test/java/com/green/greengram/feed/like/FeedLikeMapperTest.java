package com.green.greengram.feed.like;

import com.green.greengram.TestUtils;
import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeVo;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") //yaml 적용되는 파일 선택 (application-test.yml)
@MybatisTest //Mybatis Mapper Test이기 때문에 작성 >> Mapper들이 전부 객체화
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트 기본적으로 메모리 데이터베이스 (H2)를 사용해서 하는데 메모리 데이터베이스로 교체하지 않겠다.
//즉, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
//@TestInstance(TestInstance.Lifecycle.PER_CLASS) --> Test객체를 딱 하나만 만든다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedLikeMapperTest {

    @Autowired //TODO: 스프링 컨테이너가 DI해주는게 맞는지 확인
    FeedLikeMapper feedLikeMapper; //필드 주입 방식의 DI가 된다.

    @Autowired
    FeedLikeTestMapper feedLikeTestMapper;

    static final long FEED_ID_1 = 1L;
    static final long FEED_ID_5 = 5L;
    static final long USER_ID_2 = 2L;

    static final FeedLikeReq exitstedData = new FeedLikeReq();
    static final FeedLikeReq notExitstedData = new FeedLikeReq();
    /*
        @BeforeAll - 모든 테스트 실행 전에 최초 한번 실행
        ---
        @BeforeEach - 각 테스트 실행 전에 실행
        @Test
        @AfterEach - 각 테스트 실행 후에 실행
        ---
        @AfterAll - 모든 테스트 실행 후에 최초 한번 실행
     */

    // @BeforeAll - 테스트 메소드 실행되기 최초 딱 한번 실행이 되는 메소드
    // 테스트 메소드마다 테스트 객체가 만들어지면 BeforeAll 메소드는 static 메소드이어야 한다.
    // 한 테스트 객체가 만들어지면 non-static 메소드일 수 있다.
    @BeforeAll
    static void initData() {
        exitstedData.setFeedId(FEED_ID_1);
        exitstedData.setUserId(USER_ID_2);

        notExitstedData.setFeedId(FEED_ID_5);
        notExitstedData.setUserId(USER_ID_2);
    }

    // @BeforeEach - 테스트 메소드 실행 전에 테스트 메소드마다 실행되는 before메소드
    // before메소드

    @Test
    @DisplayName("중복된 데이터 입력시 DuplicateKeyException 발생 체크")
    void insFeedLikeDuplicateDataThrowDuplicateKeyException() {

        //then (단언, 체크)
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(exitstedData);
        }, "데이터 중복시 에러 발생되지 않음 > Unique(feed_id, user_id) 확인 바람");
    }

    @Test
    void insFeedLike() {

        //when
        List<FeedLikeVo> actualFeedLikeListBefore = feedLikeTestMapper.selFeedLikeAll(); //insert전 기존 튜플 수 (전과 후를 비교하기 위해서)
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExitstedData); //insert전 WHERE절에 PK로 데이터를 가져옴
        int actualAffectedRows = feedLikeMapper.insFeedLike(notExitstedData);
        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExitstedData); //insert후 WHERE절에 PK로 데이터를 가져옴
        List<FeedLikeVo> actualFeedLikeListAfter = feedLikeTestMapper.selFeedLikeAll(); //insert후 튜플 수

        //then
        assertAll(
                () -> TestUtils.assertCurrentTimestamp(actualFeedLikeVoAfter.getCreatedAt())
               ,() -> assertEquals(actualFeedLikeListBefore.size() + 1, actualFeedLikeListAfter.size())
               ,() -> assertNull(actualFeedLikeVoBefore) //내가 insert하려고 하는 데이터가 없었는지 단언
               ,() -> assertNotNull(actualFeedLikeVoAfter) //실제 insert가 내가 원하는 데이터로 되었는지 단언
               ,() -> assertEquals(1,actualAffectedRows)
               ,() -> assertEquals(notExitstedData.getFeedId(), actualFeedLikeVoAfter.getFeedId()) //내가 원하는 데이터로 insert 되었는지 더블 체크
               ,() -> assertEquals(notExitstedData.getUserId(), actualFeedLikeVoAfter.getUserId()) //내가 원하틑 데이터로
        );
    }

    @Test
    void delFeedLikeNoData() {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_5);
        givenParam.setUserId(USER_ID_2);

        int actualAffectedRows = feedLikeMapper.delFeedLike(notExitstedData);

        assertEquals(0, actualAffectedRows);
    }

    @Test
    void delFeedLike() {
        FeedLikeVo actualDelFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(exitstedData);
        int actualAffectedRows = feedLikeMapper.delFeedLike(exitstedData);
        FeedLikeVo actualDelFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExitstedData);

        assertAll(
                () -> assertEquals(1,actualAffectedRows)
               ,() -> assertNotNull(actualDelFeedLikeVoBefore)
               ,() -> assertNull(actualDelFeedLikeVoAfter)
        );
    }
}