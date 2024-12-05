package com.green.greengramver.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.green.greengramver.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
public class FeedCommentGetReq{
    private final static int FIRST_COMMENT_SIZE = 3;

    @Schema(title = "피드 PK", name="feed_id",example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @JsonIgnore
    private int startIdx;

    @JsonIgnore
    private int size;


    @ConstructorProperties({"feed_id", "start_idx", "size"})
    public FeedCommentGetReq(long feedId, int startIdx, Integer size) {
        this.feedId = feedId;
        this.startIdx = startIdx;
        this.size = (size == null ? Constants.getDefault_page_size() : size) + 1;
    }
}