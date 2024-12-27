package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.common.exception.CustomException;
import com.green.greengram.common.exception.FeedErrorCode;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.MinValidatorForBigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicMapper feedPicMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final MyFileUtils myFileUtils;
    private final AuthenticationFacade authenticationFacade;

    @Transactional //메서드 실행이 완료되면 트랜잭션을 커밋하거나 예외가 발생하면 롤백한다.
    //자동 커밋 종료
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {
        p.setWriterUserId(authenticationFacade.getSignedUserId());
        log.info("p:{}", p.toString());
        int result = feedMapper.insFeed(p);
        if(result == 0) {
            throw new CustomException(FeedErrorCode.FAIL_TO_REG);
        }

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
            } catch (IOException e) {
                //폴더 삭제 처리
                String delForderPath = String.format("%s/%s", myFileUtils.getUploadPath(), middlePath);
                myFileUtils.deleteFolder(delForderPath, true);
                throw new CustomException(FeedErrorCode.FAIL_TO_REG);
            }
        }
        FeedPicDto feedPicDto = new FeedPicDto();
        //멤버필드갯수만큼 셋팅
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);
        int resultPics = feedPicMapper.insFeedPics(feedPicDto);

        return FeedPostRes.builder()
                .feedId(feedId)
                .pics(picNameList)
                .build();
    }

    public List<FeedGetRes> getFeedList(FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //N + 1 이슈 발생 --> 한번 가져온 이후 FOR문으로 크기만큼 도니까 4번돌게 5번 돌게 된다.(처리속도도 느려진다.)
        //만약 Feed가 20개라면 처음 호출할 때 1번 FOR문안에서 사진 20번 댓글 20번해서 select 41번을 하게된다.
        List<FeedGetRes> feedGetResList = feedMapper.selFeedList(p);
        for (FeedGetRes res : feedGetResList) {
            //피드 당 사진 리스트
            List<String> resList = feedPicMapper.selFeedPicList(res.getFeedId());
            res.setPics(resList);

            //피드 당 댓글 4개
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(res.getFeedId(), 0, 3);
            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq); // 0에서 4까지

            FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
            commentGetRes.setCommentList(commentList);
            commentGetRes.setMoreComment(commentList.size() == commentGetReq.getSize());

            if (commentGetRes.isMoreComment()) { //size가 4개가 넘는다면 빼는 과정
                commentList.remove(commentList.size() - 1);
            }
            res.setComment(commentGetRes);
        }
        return feedGetResList;
    }

    //select 2번
    public List<FeedGetRes> getFeedList2(FeedGetReq p) {
        List<FeedGetRes> list = new ArrayList<>(p.getSize());

        //SELECT (1) : feed + feed_pic
        List<FeedAndPicDto> feedAndPicDtoList = feedMapper.selFeedWithPicList(p);
        FeedGetRes beforeFeedGetRes = new FeedGetRes();
        for(FeedAndPicDto feedAndPicDto : feedAndPicDtoList) {
            if(beforeFeedGetRes.getFeedId() != feedAndPicDto.getFeedId()) {
            beforeFeedGetRes = new FeedGetRes();
            beforeFeedGetRes.setPics(new ArrayList<>(3));
            list.add(beforeFeedGetRes);
            beforeFeedGetRes.setFeedId(feedAndPicDto.getFeedId());
            beforeFeedGetRes.setContents(feedAndPicDto.getContents());
            beforeFeedGetRes.setLocation(feedAndPicDto.getLocation());
            beforeFeedGetRes.setCreatedAt(feedAndPicDto.getCreatedAt());
            beforeFeedGetRes.setWriterUserId(feedAndPicDto.getWriterUserId());
            beforeFeedGetRes.setWriterNm(feedAndPicDto.getWriterNm());
            beforeFeedGetRes.setWriterPic(feedAndPicDto.getWriterPic());
            beforeFeedGetRes.setIsLIKE(feedAndPicDto.getIsLIKE());
            }
            beforeFeedGetRes.getPics().add(feedAndPicDto.getPic());
        }

        //SELECT (2) : feed_comment
        List<Long> feedIds = new ArrayList<>(list.size());
        for(FeedGetRes item : list) {
            feedIds.add(item.getFeedId());
        }
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>();
        for(FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if(!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>(4));
                commentHashMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId);
            feedCommentGetRes.getCommentList().add(item);
        }
        for(FeedGetRes res : list) {
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if(feedCommentGetRes == null) {
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }
        return list;
    }

    //select 3번, 피드 5000개 있음, 페이지당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 리스트
        List<FeedGetRes> list = feedMapper.selFeedList(p);
        if(list.size() == 0) {
            return list;
        }

        //feed_id를 골라내야 한다.
        //List<Long> feedIds = list.stream().map(FeedGetRes::getFeedId).collect(Collectors.toList());
        //List<Long> feedId2 = list.stream().map(item -> ((FeedGetRes)item).getFeedId()).toList();
        //List<Long> feedId3 = list.stream().map(item -> {return ((FeedGetRes)item).getFeedId();}).toList();

        //아래와 같은 효과
        List<Long> feedIds = new ArrayList<>(list.size());
        for (FeedGetRes item : list) {
            feedIds.add(item.getFeedId());
        }

//       int lastIndex = 0;
//        for (FeedGetRes res : list) {
//            List<String> pics = new ArrayList<>(2);
//            for (int i = lastIndex; i < feedPicList.size(); i++) {
//                FeedPicSel feedPicSel = feedPicList.get(i);
//                if (res.getFeedId() == feedPicSel.getFeedId()) {
//                    pics.add(feedPicSel.getPic());
//                } else {
//                    res.setPics(pics);
//                    lastIndex = i;
//                    break;
//                }
//            }
//        }

        //피드와 관련된 사진 리스트
        List<FeedPicSel> feedPicList = feedPicMapper.selFeedPicListByFeedIds(feedIds);
        log.info("feedPicList: {}", feedPicList);

        Map<Long, List<String>> picHashMap = new HashMap<>();
        for(FeedPicSel item : feedPicList) {
            long feedId = item.getFeedId();
            if(!picHashMap.containsKey(feedId)) {
                picHashMap.put(feedId, new ArrayList<String>(2));
            }
            List<String> pics = picHashMap.get(feedId);
             pics.add(item.getPic());
        }

        for (FeedGetRes res : list) {
            res.setPics(picHashMap.get(res.getFeedId()));
        }


        //피드와 관련된 댓글 리스트

        //댓글이 있는 경우만 정리
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>();
        for(FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if(!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
                commentHashMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId);
            feedCommentGetRes.getCommentList().add(item);
        }

        for(FeedGetRes res : list) {
            res.setPics(picHashMap.get(res.getFeedId()));
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if(feedCommentGetRes == null) {
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }
        log.info("list: {}", list);
        return list;
    }

    public List<FeedGetRes> getFeedList4(FeedGetReq p) {
        List<FeedWithPicCommentDto> dtoList = feedMapper.selFeedWithPicAndCommentLimit4List(p);
        List<FeedGetRes> resList = new ArrayList<>();
        for(FeedWithPicCommentDto item : dtoList) {
            FeedGetRes feedGetRes = new FeedGetRes(item);
            resList.add(feedGetRes);
        }
        return resList;
    }

    @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 댓글, 좋아요, 사진 삭제
        int affectedRows = feedMapper.delFeedLikeAndFeedCommentAndFeedPic(p);
        log.info("affectedRows:{}", affectedRows);

        //피드 삭제
        int affectedRow = feedMapper.delFeed(p);
        log.info("affectedRow:{}", affectedRow);

        //피드 사진 삭제 (폴더 삭제)
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);

        return 1;
    }
}