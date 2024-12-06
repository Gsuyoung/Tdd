package com.green.greengram.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//생성자도 메소드에 한 종류이다. --> 특별한이유 : 호출할 때만 만들 수 있다. (클래스명이랑 동일하고 리턴타입이 없다.)
@Slf4j
@Component //빈등록
public class MyFileUtils {
    private final String uploadPath; //uploadpath에 저장되어있는 값 : D:/GSY/download/greengram_ver1

    /*
    @Value("${file.directory}")은 yaml 파일에 있는 file.directory 속성에 저장된 값을 생성자 호출할 때 값을 넣어준다.
    DI한것
     */
    public MyFileUtils(@Value("${file.directory}") String uploadPath) {
        log.info("MyFileUtils - 생성자: {}", uploadPath);
        this.uploadPath = uploadPath;
    }

    //디렉토리 생성
    public String makeFolders(String path) {
        File file = new File(uploadPath, path); //new 다음에 부를 수 있는 것 : 생성자
        // static 아님 --> 객체화하고 주소값.(file.)으로 호출했기 때문에
        // 리턴타입은 boolean --> if()안에서 호출했기 때문에
        // 파라미터는 없음 --> 호출 때 인자를 보내지 않았기 때문에
        // 메소드명은 --> exists였다.
        if(!file.exists()) { //--> 파일이 존재하지 않는다면...
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    //파일명에서 확장자 추출
    public String getExt(String fileName) {
        int lastIdx = fileName.lastIndexOf(".");
        return fileName.substring(lastIdx);
    }

    //랜덤파일명 생성
    //오버로딩
    public String makeRandomFileName() {
        return UUID.randomUUID().toString();
    }

    //랜덤파일명 + 확장자 생성
    public String makeRandomFileName(String originalFileName) {
        return makeRandomFileName() + getExt(originalFileName); //<--옆정보만보고 알수있다.(void가아닌 return메소드 return하는것자체가 데이터를 담고있으므로)
    }

    //MultipartFile 타입은 프론트에서 사진을 받기위해 사용(처리를 다르게해야한다.)
    public String makeRandomFileName(MultipartFile file) {
        return makeRandomFileName(file.getOriginalFilename()); // OriginalFilename을 부른이유 : 확장자를 얻기위해서
                                                                //위에 리턴메소드이다.
    }

    //파일을 원하는 경로에 저장
    public void transferTo(MultipartFile mf, String path) throws IOException {
        File file = new File(uploadPath, path);
        mf.transferTo(file);
    }
}

    class Test {
        public static void main(String[] args) {
            MyFileUtils myFileUtils = new MyFileUtils("C:/temp");
            String randomFileName = myFileUtils.makeRandomFileName("download.jpg");
            System.out.println(randomFileName);
        }
}