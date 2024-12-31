package com.green.greengram.feed.like.model;

import lombok.Getter;

//@Setter 메소드가 없어서 Reflection API를 이용하여 데이터가 대입이 될건데
//이때 중요한게 객체 생성이 무조건 되어야 한다. 그래서 기본 생성자가 있어야 한다.
@Getter
public class FeedPicVo {
    private long feedId;
    private String pic;
    private String createdAt;
}