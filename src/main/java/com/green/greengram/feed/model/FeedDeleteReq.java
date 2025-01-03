package com.green.greengram.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@Schema(title = "피드 DELETE 요청")
@EqualsAndHashCode
public class FeedDeleteReq {
    @Schema(description = "피드 PK", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @JsonIgnore
    private long signedUserId;

    @ConstructorProperties({"feed_id", "signed_user_id"})
    public FeedDeleteReq(long feedId) {
        this.feedId = feedId;
    }

    public void setSignedUserId(long signedUserId) {
        this.signedUserId = signedUserId;
    }
}
