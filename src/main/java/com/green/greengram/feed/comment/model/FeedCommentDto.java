package com.green.greengram.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

//Data Transfer Object(DTO)
@Getter
@Setter
@Schema(title = "피드 댓글 상세")
@EqualsAndHashCode
public class FeedCommentDto {
    @JsonIgnore
    private long feedId;

    private long feedCommentId;
    private long writerUserId;
    private String comment;
    private String writerNm;
    private String writerPic;
}
