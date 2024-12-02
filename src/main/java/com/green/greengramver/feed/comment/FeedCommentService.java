package com.green.greengramver.feed.comment;

import com.green.greengramver.feed.FeedMapper;
import com.green.greengramver.feed.FeedService;
import com.green.greengramver.feed.comment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;

    public Long postFeedComment(FeedCommentPostReq p) {
        //int result = mapper.insFeedComment(p);
        //return p.getFeedCommentId();

        mapper.insFeedComment(p);
        Long result = p.getFeedCommentId();
        return result;
    }
}
