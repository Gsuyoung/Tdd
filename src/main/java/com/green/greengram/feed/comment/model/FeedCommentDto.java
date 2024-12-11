package com.green.greengram.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

//Data Transfer Object(DTO)
@Getter
@Setter
public class FeedCommentDto {
    @JsonIgnore
    private long feedId;

    private long feedCommentId;
    private long writerUserId;
    private String comment;
    private String writerNm;
    private String writerPic;
}
