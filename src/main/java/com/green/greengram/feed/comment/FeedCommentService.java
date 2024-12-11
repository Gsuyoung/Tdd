package com.green.greengram.feed.comment;

import com.green.greengram.feed.comment.model.*;
import com.green.greengram.feed.model.FeedGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;

    //피드 댓글 더보기를 위해서 사용
    public Long postFeedComment(FeedCommentPostReq p) {
        //int result = mapper.insFeedComment(p);
        //return p.getFeedCommentId();

        mapper.insFeedComment(p);
        Long result = p.getFeedCommentId();
        return result;
    }

    public FeedCommentGetRes getFeedComment(FeedCommentGetReq p) {
        FeedCommentGetRes res = new FeedCommentGetRes();
        if(p.getStartIdx() < 0) {
            res.setCommentList(new ArrayList<>());
            return res;
        }
        List<FeedCommentDto> commentList = mapper.selFeedCommentList(p); //1~21사이
        res.setCommentList(commentList);
        res.setMoreComment( commentList.size() == p.getSize() );
        if(res.isMoreComment()) {
            commentList.remove(commentList.size() - 1);
        }
        return res;
    }

    public int delFeedComment(FeedCommentDelReq p) {
        return mapper.delFeedComment(p);
    }
}