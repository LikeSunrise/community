package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.utils.CommunityUtils;
import com.newcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }


    /**
     * 处理 更新头像
     * 思路：更新头像的时候，会让上传一张图，这张图是我自己电脑，也就是本地硬盘上的，上传后
     * 我们做的是，将这张图存储在自己电脑的另一个upload 的文件夹中，然后就涉及一个问题，
     * 页面刷新的时候要显示更新后的头像，做法是，当有页面请求的时候，设置该请求下的方法这样处理：
     * 去电脑本地上找upload的文件夹，然后把文件读取出来，然后显示在页面。所以其实这里存在本地硬盘
     * 就是存在 服务器上，因为localhost服务器就是本机，更好的方法是把图片存储在云服务器上，然后访问他，
     * 这是后续内容。
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","你还没有上传文件！！");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","你上传的文件格式不对！！");
            return "/site/setting";
        }

        //生成随机的文件名，这样才能保证上传的各个文件名不同，然后存入硬盘
        filename = CommunityUtils.generateUUID() + suffix;

        /**
         * File对象就是声明一个文件，这个文件是你将要存储的硬盘的地址，相当于声明一块存储空间，去存
         * 要存储的东西
         */
        File dest = new File(uploadPath + "/" + filename);

        //把当前文件写入硬盘
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传你失败 ：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！",e);
        }

        //更新用户头像的地址，这里的地址是web地址，也就是别人在外部访问的地址
        //规定为这样的格式：http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headUrl);

        return "redirect:/index";
    }

    //页面获取用户头像并显示（因为这里用户头像更新了，所以需要显示更新后的）
    @GetMapping("/header/{filename}")
    public void getHeader(HttpServletResponse response,
                            @PathVariable("filename") String filename ){

        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/" + suffix);

        try(
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while( (b=fis.read(buffer)) != -1 ){
                os.write(buffer,0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }
}
