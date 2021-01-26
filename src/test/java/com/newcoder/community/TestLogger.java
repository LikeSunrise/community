package com.newcoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestLogger {

    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("---------debug log");
        logger.info("---------info log");
        logger.error("---------error log");
        logger.warn("---------warn log");
    }
}
