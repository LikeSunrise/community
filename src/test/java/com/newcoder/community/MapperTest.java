package com.newcoder.community;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.utils.CommunityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;


@SpringBootTest
public class MapperTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void testSelect(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("zhangfei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }


    @Test
    void testInsert(){
        User user = new User();
        user.setUsername("吴瑞文");
        user.setPassword("wrw198120");
        user.setSalt("abc");
        user.setEmail("csu123.com");
        user.setHeaderUrl("https://www.newcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println("插入的行数 = "+rows);
        System.out.println("id = "+user.getId());
    }


    @Test
    void testUpdate(){
        int rows = userMapper.updatePassword(150,"wrw198120修改2");
        System.out.println("插入的行数 = "+rows);

        int row2 = userMapper.updateStatus(150,1);
        row2 = userMapper.updateHeader(150,"https://www.baidu.com");
    }


    @Test
    void testSelectDiscussPosts(){

        List<DiscussPost> discussPostLists = discussPostMapper.selectDiscussPosts(0,0,10);
//        System.out.println(discussPostLists);
        for (DiscussPost discussPost:discussPostLists){
            System.out.println(discussPost);
        }

        System.out.println("---------------------------------------------------------------------");
        int res = discussPostMapper.selectDiscussPostRows(138);
        System.out.println("res = \n" + res);
    }


    @Test
    public void testInsertOfLoginTicketMapper(){

        //测试insert
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(123);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    public void testSelectOfLoginTicketMapper(){
        //测试select
        String ticket = "abc";
        LoginTicket lgticket = new LoginTicket();
        lgticket = loginTicketMapper.selectByTicket(ticket);
        System.out.println("lgticket = " + lgticket);
    }

    @Test
    public void testUpdateOfLoginTicketMapper(){
        //测试update
        System.out.println(loginTicketMapper.selectByTicket("abc"));
        loginTicketMapper.updateStatus("abc",1);
        System.out.println(loginTicketMapper.selectByTicket("abc"));

    }
}
