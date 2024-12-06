package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.feed.comment.FeedCommentController;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicsMapper feedPicsMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final MyFileUtils myFileUtils;
    private final FeedCommentController feedCommentController;

    @Transactional //메서드 실행이 완료되면 트랜잭션을 커밋하거나 예외가 발생하면 롤백한다.
    public FeedPostRes postFeed (List<MultipartFile> pics, FeedPostReq p) {
        log.info("p:{}",p.toString());
        int result = feedMapper.insFeed(p);
        log.info("p:{}",p.toString());

        // -------------- 파일등록
        long feedId = p.getFeedId();

        // 저장 폴더 만들기 : 저장위치/feed/#{feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

        //랜덤 파일명 저장 --> feed_pics 테이블에 저장할 때 사용
        //pics.size() 넣은 이유 --> for문을 돌리면 돌릴때마다 배열크기를 증가시켜야하는데 이렇게 사용하면
        //처음부터 크기를 정하여 셋팅할수있기때문에 효율이 좋다.
        List<String> picNameList = new ArrayList<>(pics.size());

        for (MultipartFile pic : pics) {
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedPicName);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pic, filePath);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        FeedPicDto feedPicDto = new FeedPicDto();
        //멤버필드갯수만큼 셋팅
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);
        int resultPics = feedPicsMapper.insFeedPics(feedPicDto);

        return FeedPostRes.builder()
                          .feedId(feedId)
                          .pics(picNameList)
                          .build();
    }

    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        //N + 1 이슈 발생 --> 한번 가져온 이후 FOR문으로 크기만큼 도니까 4번돌게 5번 돌게 된다.(처리속도도 느려진다.)
        List<FeedGetRes> feedGetResList = feedMapper.selFeedList(p);
        for (FeedGetRes res : feedGetResList) {
            //피드 당 사진 리스트
            List<String> resList = feedPicsMapper.selFeedPicList(res.getFeedId());
            res.setPics(resList);

            //피드 당 댓글 4개
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(res.getFeedId(),0,3);
            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq); // 0에서 4까지

            FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
            commentGetRes.setCommentList(commentList);
            commentGetRes.setMoreComment(commentList.size() == commentGetReq.getSize());

            if(commentGetRes.isMoreComment()) { //size가 4개가 넘는다면 빼는 과정
                commentList.remove(commentList.size() - 1 );
            }
            res.setComment(commentGetRes);
        }
        return feedGetResList;
    }
}