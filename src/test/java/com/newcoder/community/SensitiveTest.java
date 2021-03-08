package com.newcoder.community;

import com.newcoder.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTest {


    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter() {

        String text = "卧槽，蔡徐坤和肖战的家人们，快来这里赌博，也可以嫖娼哦，哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println("text = " + text);

        text = "卧槽，我的大傻△逼们，☆蔡☆徐☆坤和☆肖☆战☆的家人们，快来这里☆赌☆博☆，也可以☆嫖☆娼☆哦，哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println("text = " + text);
    }
}
