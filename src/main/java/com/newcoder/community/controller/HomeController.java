package com.newcoder.community.controller;

import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){

        page.setPath("/index");
        page.setRows(discussPostService.findDiscussionPostRows(0));

        List<DiscussPost> list = discussPostService.findDiscussionPosts(0,page.getOffset(),page.getLimit());
        /**
         * 要把list重新封装以下，因为我想要的list是包含用户发表的post，还有用户名的，
         * 但是post质保函userId，还不是一个user对象，可以封装成DTO，最简单的是
         * 把需要的东西都放在map里面就是了，所以discussPosts就是一个包含多个map的数组，而这个map又包含user+post的key
         */
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
}

/*
getIndexPage请求是这样处理的：
比如，html页面中，点击首页，这个首页是给了它封装了一个地址的，所以必然会跳转至那个页面，
然而这个地址是/index?...实质是调用了getIndexPage这个请求处理方法，因为@GetMapping("/index")控制了getIndexPage()
所以每次点击"首页"，就会执行这个函数，这个函数实质就是 从当前页的页码，即为current，从数据库中抽取limit条记录
显示在页面上来，即：这一句
List<DiscussPost> list = discussPostService.findDiscussionPosts(0,page.getOffset(),page.getLimit());
 2021年1月26日15:10:06
 */