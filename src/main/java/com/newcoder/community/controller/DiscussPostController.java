package com.newcoder.community.controller;

import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.utils.CommunityUtils;
import com.newcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){

        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtils.getJSONString(403,"你还没有登陆哦！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtils.getJSONString(0,"发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId,
                                 Model model){

        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //查询帖子的作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }
}
