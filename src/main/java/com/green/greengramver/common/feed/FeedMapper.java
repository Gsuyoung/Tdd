package com.green.greengramver.common.feed;

import com.green.greengramver.common.feed.model.FeedPostReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedMapper {
    int insFeed(FeedPostReq p);
}
