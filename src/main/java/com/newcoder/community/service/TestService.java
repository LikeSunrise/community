package com.newcoder.community.service;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import com.newcoder.community.utils.CommunityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TestService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
            //新增用户
            User user = new User();
            user.setUsername("巴嘎拉");
            user.setSalt(CommunityUtils.generateUUID().substring(0,5));
            user.setEmail("123@qq.com");
            user.setPassword(CommunityUtils.md5("123"+user.getSalt()));
            user.setCreateTime(new Date());
            user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
            userMapper.insertUser(user);

        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setCreateTime(new Date());
        post.setContent("新人报道！");
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }


}
