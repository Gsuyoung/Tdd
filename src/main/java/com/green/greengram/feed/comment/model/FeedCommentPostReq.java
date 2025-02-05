package com.green.greengram.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@Schema(title = "피드 댓글 등록 요청") //GET방식일때는 title을 사용하지 못한다.
public class FeedCommentPostReq {
    @JsonIgnore
    private Long feedCommentId; //service에서 pk값으로 리턴하기위해 필요하다.

    @Schema(title = "피드 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @JsonIgnore
    private long userId;

    @Schema(title = "댓글 내용", example = "댓글입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;
}