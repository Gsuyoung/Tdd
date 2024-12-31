package com.green.greengram.feed.like;

import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedLikeTestMapper {
    //객체가 하나일때는 null값이나 값이 넘어온다.
    @Select("SELECT * FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}")
    FeedLikeVo selFeedLikeByFeedIdAndUserId(FeedLikeReq p);

    //feed_like에 튜플이 하나도 없다면 List일때는 null값이 넘어오는 것이아니라 size가 0인 값이 넘어온다.
    @Select("SELECT * FROM feed_like")
    List<FeedLikeVo> selFeedLikeAll();
}
