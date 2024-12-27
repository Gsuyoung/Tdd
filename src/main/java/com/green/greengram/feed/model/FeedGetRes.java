package com.green.greengram.feed.model;

import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Schema(title = "피드 정보")
@NoArgsConstructor //기본생성자를 만들어 준다.
public class FeedGetRes {
    private long feedId;
    private long writerUserId;
    private String writerNm;
    private String writerPic;
    private String createdAt;
    private String contents;
    private String location;
    private int isLIKE;

    private List<String> pics;
    private FeedCommentGetRes comment;

    public FeedGetRes(FeedWithPicCommentDto dto) {
        this.feedId = dto.getFeedId();
        this.contents = dto.getContents();
        this.location = dto.getLocation();
        this.createdAt = dto.getCreatedAt();
        this.writerUserId = dto.getWriterUserId();
        this.writerNm = dto.getWriterNm();
        this.writerPic = dto.getWriterPic();
        this.isLIKE = dto.getIsLike();
        this.pics = dto.getPics();
        this.comment = new FeedCommentGetRes();

        //TODO : 댓글 moreComment, list 컨버트
        if(comment == null) {
            comment = new FeedCommentGetRes();
            comment.setCommentList(new ArrayList<>());
        } else if (comment.getCommentList().size() == 4) {
            comment.setMoreComment(true);
            comment.getCommentList().remove(comment.getCommentList().size() - 1);
        }
    }
}
