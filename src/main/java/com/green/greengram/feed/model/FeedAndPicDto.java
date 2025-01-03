package com.green.greengram.feed.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class FeedAndPicDto {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerNm;
    private String writerPic;
    private int isLIKE;
    private String pic;
}
