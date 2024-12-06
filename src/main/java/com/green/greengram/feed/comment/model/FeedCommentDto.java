package com.green.greengram.feed.comment.model;

import lombok.Getter;
import lombok.Setter;

//Data Transfer Object(DTO)
@Getter
@Setter
public class FeedCommentDto {
    private long feedCommentId;
    private long writerUserId;
    private String comment;
    private String writerNm;
    private String writerPic;
}
