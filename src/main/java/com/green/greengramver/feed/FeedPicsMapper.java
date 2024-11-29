package com.green.greengramver.feed;

import com.green.greengramver.feed.model.FeedPicDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedPicsMapper {
    int insFeedPics(FeedPicDto p);
    int insFeedPics2(FeedPicDto p);
    List<String> selFeedPicList(long feedId);
}
