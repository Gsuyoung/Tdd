package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedServiceParentTest {
    @Mock protected FeedMapper feedMapper;
    @Mock protected FeedPicMapper feedPicMapper;
    @Mock protected FeedCommentMapper feedCommentMapper;
    @Mock protected MyFileUtils myFileUtils;
    @Mock protected AuthenticationFacade authenticationFacade;
    @InjectMocks protected FeedService feedService;

    final long FEED_ID_10 = 10L;
    final long SIGNED_USER_ID = 3L;
    final String LOCATION = "테스트 위치";
}
