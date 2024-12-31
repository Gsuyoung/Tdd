package com.green.greengram.feed.like.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/*
    immutable(불변성)하게 객체를 만들고 싶다. 그러면 setter를 빼야한다.
    private한 멤버필드에 값 넣는 방법 2가지 (생성자, setter)
    setter를 빼기로 했기 때문에 남은 선택지는 생성자만 남았다.
    생성자를 이용해서 객체 생성을 해야 하는데 멤버필드값을 세팅하는 경우의 수가 많을 수 있다.
    1.feedId만 세팅한다.
    2.userId만 세팅
    3.createdAt만 세팅
    4.(feedId, userId)
    5.(feedId,createdAt)
    6.(userId,createdAt)
    7.feedId,userId,createdAt
    8.하나도 세팅X
 */

@Getter
@Builder
@EqualsAndHashCode //동등성 <-- 오버라이딩을 해준다.
public class FeedLikeVo { //@Setter를 뺀 이유는 immutable하게 만들기위해서
    private long feedId;
    private long userId;
    private String createdAt;

    //오버라이딩은 부모가 가지고 있는 메소드 선언부를 똑같이 적어야한다.(부모 클래스의 메소드를 자식 클래스에서 재정의하는 것.)

    //오버로딩은 같은 클래스 내에서 메소드 이름은 같지만 매개변수가 다른 메소드를 정의하는 것.
}
