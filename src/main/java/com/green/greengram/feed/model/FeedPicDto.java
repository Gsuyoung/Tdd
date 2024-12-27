package com.green.greengram.feed.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
/*
    feed_pics 테이블에 튜플 여러개를 insert
    한 문장으로 처리하기 위해 사용하는 객체
 */
public class FeedPicDto {
    private long feedId;
    private List<String> pics;
}
