package com.newcoder.community.controller;

import com.newcoder.community.utils.CommunityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class TestController {

    /**
     * Cookie生成，测试
     */


    //例如，浏览器给出请求 http://localhost:8080/community/cookie/set，就会执行下列
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){

        //创建 cookie
        Cookie cookie = new Cookie("code", CommunityUtils.generateUUID());
        /**
         * 设置cookie生效的范围
         * Cookie:小明第一次去银行存钱，银行给了小明一张会员卡，小明第二次去银行存钱，拿着这张
         * 会员卡，银行得知小明上次来过，即银行认识小明
         * 小明：客户端
         * 银行：服务端
         * cookie生效范围的意思就是小明第二次去银行的这个有效路径，比如会员卡是建行发的，
         * 小明这次去农行，肯定不行，这里有效范围就是 "/建设银行",当然"/建设银行/A区"
         * 子路径也是可以的
         */
        cookie.setPath("/community/cookie");
        //设置cookie的有效生命时间
        cookie.setMaxAge(60 * 10);
        //cookie是在服务端产生，并通过 HttpServletResponse从服务端携带至浏览器的
        response.addCookie(cookie);
        return "set cookie successfully!";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println("code =  "+ code );
        return "get cookie successfully";
    }

    /**
     * session demo
     * 浏览器（客户端）中拿到的sessionID一般显示为
     * JSESSIONID=E80EB077F3FB4E705D4057AD612D0557; 即为JSESSIONID
     */

    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id","123456");
        session.setAttribute("name","吴瑞文");
        return "set session successfully";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));

        return "get session successfully";
    }
}
