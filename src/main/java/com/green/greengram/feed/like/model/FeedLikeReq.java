package com.green.greengram.feed.like.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(title = "피드 좋아요 Toggle")
public class FeedLikeReq {
    @Schema(title = "피드 PK", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;
    @Schema(title = "유저 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;
}
