package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.utils.CookieUtils;
import com.newcoder.community.utils.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
/**
 * 登陆拦截器
 * 在处理登陆请求之前，我们希望能够直接在页面有显示用户登录的信息（比如，头像等）
 * 方法：我们应该从浏览器中获取 cookie，拿到和用户相关联的的登录凭证ticket，
 * 通过这个ticket去查表得到对应的 user
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从浏览器中拿取cookie,从cookie中拿到名为 ticket 的 cookie
        String ticket = CookieUtils.getCookieValue(request,"ticket");
        if(ticket != null){//说明这个ticket对应的user已经登录了
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断这个用户登录凭证是否有效，(凭证过期超时)
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //如果登录凭证没有过期，那么就通过凭证查找这个用户
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }
    //现在我们要做的就是在thymeleaf渲染之前，把user信息存在model中，然后执行ThymeleEngine去渲染

    @Override //这个方法恰好就是在ThymeleafEngine之前调用的，而且有model可以用来存信息
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView !=null ){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
