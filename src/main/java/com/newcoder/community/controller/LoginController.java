package com.newcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.utils.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){

        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        /**
         * 将验证码存入session
         * 验证码包含2个东西，一是用于验证的text，还有就是显示在页面的这个text“加密”后的image，
         * 显然 text 是用于验证正确与否的，是敏感信息，不能存在客户端，
         * 要存储在服务端，因此用session
         */
        session.setAttribute("kaptcha",text);

        //将图片响应给浏览器
        response.setContentType("image/png");//设置响应编码，常见有 text/html
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            //e.printStackTrace();
            logger.error("服务器响应验证码失败！" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public String register(Model model , User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            /**
             * //map为空，说明注册成功！注册成功，则跳转到operate-result.html页面（很好的操作结果处理页面模板），
             * 然后执行激活操作，然后再跳转到登陆页面
             */
            model.addAttribute("msg","您已注册成功，我们已向你的邮箱发送了激活码，请尽快激活！！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{//map不为空，说明注册失败！,则仍然跳转到注册页面继续注册,并且用model携带必要信息
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    @PostMapping("/login")
    public String login(String username,String password,String code, boolean rememberme,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model
                        ){

        /**
         * 检查验证码(其实先后检查验证码和账户和密码都无所谓)
         */
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        /**
         * 检查账户和密码
         * 为什么要拿到过期时间？这里的逻辑是什么样的？
         * 这里拿到过期时间，就相当于，想象以下登陆场景，我必须知道我登录过期时间，登陆之后在这个时间内都不用登陆了，就是这意思
         * 而这个逻辑也是LoginTicket所封装的
         * 所以说，这里的验证码的验证就没有用业务层，直接是拿session中的数据来比对，而验证账户和密码就用到了业务层UserService
         * 始终记住：controller层要做的就是拿到错误信息，存在model中，然后用于前端渲染
         *       而 service业务层要做的是真正去处理那些从前端页面获取的数据
         */
        int expireSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expireSeconds);
        if(map.containsKey("ticket")){  //如果登陆成功了
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expireSeconds);
            response.addCookie(cookie); //存储cookie到页面（浏览器）是响应过程，即response过程
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }


    @GetMapping("/logout")  //CookieValue直接从浏览器中读取cookie
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
