package com.newcoder.community;

import com.newcoder.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("3176289501@qq.com","TEST","welcome qq email!!");
    }

    @Test
    public void testHtmlMail(){

        // Context是要传入Template模板引擎的参数
        Context context = new Context();
        context.setVariable("username","大傻逼");
        context.setVariable("id","123456");
        //生成要发送的内容，就是把参数掺进html页面，因为html页面有待要填补的参数username
        String content = templateEngine.process("/mail/demo",context);
        System.out.println("content = " + content);
        mailClient.sendMail("3176289501@qq.com","HTML_TEST",content);

    }
}
