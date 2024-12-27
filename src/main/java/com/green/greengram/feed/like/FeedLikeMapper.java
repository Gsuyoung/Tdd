package com.green.greengram.feed.like;

import com.green.greengram.feed.like.model.FeedLikeReq;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedLikeMapper {
    int insFeedLike(@Valid FeedLikeReq p);
    int delFeedLike(@Valid FeedLikeReq p);
}
