package com.green.greengram.feed.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FeedPicDto {
    private long feedId;
    private List<String> pics;
}
