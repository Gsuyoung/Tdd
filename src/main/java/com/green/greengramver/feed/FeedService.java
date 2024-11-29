package com.green.greengramver.feed;

import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.feed.model.*;
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
    private final MyFileUtils myFileUtils;

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
        //pics.size() 넣은 이유 --> for문을 돌리면 돌릴때마다 배열크기를증가시켜야하는데 이렇게 사용하면
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
        List<FeedGetRes> feedGetResList = feedMapper.selFeedList(p);
        for (FeedGetRes res : feedGetResList) {
            List<String> resList = feedPicsMapper.selFeedPicList(res.getFeedId());
            res.setPics(resList);
        }
        return feedGetResList;
    }
}