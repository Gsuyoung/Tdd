package com.green.greengramver.feed.comment;

import com.green.greengramver.common.model.ResultResponse;
import com.green.greengramver.feed.comment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/comment")
public class FeedCommentController {
    private final FeedCommentService service;


    @PostMapping
    public ResultResponse<Long> postFeedComment(@RequestBody FeedCommentPostReq p) {
        Long result = service.postFeedComment(p);
        return ResultResponse.<Long>builder()
                             .resultMsg("댓글등록완료")
                             .resultData(result)
                             .build();
    }
}