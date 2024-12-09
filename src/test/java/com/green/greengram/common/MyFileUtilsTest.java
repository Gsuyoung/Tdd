package com.green.greengram.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyFileUtilsTest {
    private final String FILE_DIRECTORY = "D:/GSY/GreenGramVer3/greengram_ver3";
    MyFileUtils myFileUtils;

    @BeforeEach // 한번만 객체화하면 되게 하는 것.
    void setUp() {
        myFileUtils = new MyFileUtils(FILE_DIRECTORY);
    }

    @Test
    void deleteFolder() {
        String path = String.format("%s/user/ddd", myFileUtils.getUploadPath());
        myFileUtils.deleteFolder(path,false);
    }
}