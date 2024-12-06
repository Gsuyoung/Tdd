package com.green.greengram.feed.comment;

import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.feed.comment.model.FeedCommentDelReq;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.comment.model.FeedCommentPostReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @Operation(summary = "피드 댓글 리스트", description = "댓글 더보기 처리")
    public ResultResponse<FeedCommentGetRes> getFeedComment(@ParameterObject @ModelAttribute FeedCommentGetReq p) {
        log.info("FeedCommentController > getFeedComment > p: {}", p);
        FeedCommentGetRes result = service.getFeedComment(p);
        return ResultResponse.<FeedCommentGetRes>builder().resultMsg(String.format("%d rows", result.getCommentList().size())).resultData(result).build();
    }

    @GetMapping("/request_param")
    @Operation(summary = "피드 댓글 리스트", description = "댓글 더보기 처리 - 파라미터를 RequestParam을 이용해서 받음")
    public ResultResponse<FeedCommentGetRes> getFeedComment2(@Parameter(description = "피드 PK", example = "12") @RequestParam("feed_id") long feedId
            , @Parameter(description = "튜플 시작 index", example = "3") @RequestParam("start_idx") int startIdx
            , @Parameter(description = "페이지 당 아이템 수", example = "20") @RequestParam(required = false, defaultValue = "20") int size) {
        FeedCommentGetReq p = new FeedCommentGetReq(feedId, startIdx, size);
        log.info("FeedCommentController > getFeedComment > p: {}", p);
        FeedCommentGetRes res = service.getFeedComment(p);
        return ResultResponse.<FeedCommentGetRes>builder()
                .resultMsg(String.format("%d rows", res.getCommentList().size()))
                .resultData(res)
                .build();
    }

    //삭제시 받아야 할 데이터 feedCommentId + 로그인한 사용자의 PK (feed_comment_id, signed_user_id)
    //FE - data 전달방식 : Query-String
    @DeleteMapping
    public ResultResponse<Integer> delFeedComment(@ParameterObject @ModelAttribute FeedCommentDelReq p) {
        log.info("FeedCommentController delFeedComment > p: {}", p);
        int result = service.delFeedComment(p);
        return ResultResponse.<Integer>builder().resultMsg("삭제완료").resultData(result).build();
    }
}