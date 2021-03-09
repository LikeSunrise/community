package com.newcoder.community;

import com.newcoder.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 事务回滚 测试
 */
@SpringBootTest
public class TransactionTest {

    @Autowired
    private TestService testService;

    @Test
    public void testSave1(){
        Object obj = testService.save1();
        System.out.println("obj = " + obj);
    }
}
