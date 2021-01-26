package com.newcoder.community;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;


@SpringBootTest
public class MapperTest {

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
}
