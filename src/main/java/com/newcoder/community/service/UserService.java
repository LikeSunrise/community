package com.newcoder.community.service;

import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.utils.CommunityConstant;
import com.newcoder.community.utils.CommunityUtils;
import com.newcoder.community.utils.MailClient;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //注册的时候，发送邮件需要包含激活码，激活码包含域名+项目名，这两个值都在properties文件中有初始化，因此要注入这2个值
    @Value("${community.path.domain}")
    private String domain;//相当于community.path.domain的重命名

    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserById(int id){
        return userMapper.selectById(id);
    }


    /**
     * 注册
     * @param user
     * @return
     */
    public Map<String,Object> register(User user){
        /**
         * 注意 null 和 空值的区别：
         * null是指对象为空，根本连内存都没分配
         * 空值是这个变量分配了内存，但是这块内存存储的值是空值
         */
        Map<String,Object> map = new HashMap<>();//用map保存状态信息：成功 ，失败的msg
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空！");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
        }

        //验证账号
        User db_user = userMapper.selectByName(user.getUsername());
        if(db_user != null ){
            map.put("usernameMsg","该用户名的用户已经存在！不能注册！");
            return map;
        }
        //验证邮箱
        db_user = userMapper.selectByEmail(user.getEmail());
        if(db_user != null ) {
            map.put("emailMsg","该邮箱已经被注册！不能再次被注册！");
            return map;
        }

        //通过上述步骤，如果没有问题，即账号邮箱都不为空，而且数据库不存在（没有注册过）那么就可以注册，注册的实质是写入数据库，写入之前加密即可
        //注册用户
        user.setSalt(CommunityUtils.generateUUID().substring(0,5));
        user.setPassword(CommunityUtils.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0); //0 表示未激活，这也是后面激活步骤要处理的事情 ，要把status -> 1
        user.setActivationCode(CommunityUtils.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //  %d  表示待替换的数字
        userMapper.insertUser(user);
        System.out.println("------------user id-------------: " + user.getId());

        //注册的最后一步，要发送 激活邮件 给用户，而且是以html的形式发给用户邮箱（activcation.html）

        Context context = new Context();
        context.setVariable("email",user.getEmail());

        /**
         * 我们自定义激活的路径如下：
         * http://localhost:8080/community/activation/101/code
         * 101是用户id
         * code 是激活码
         */
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    /**
     * 激活，返回激活状态码
     */
    public int activation(int userId,String code){

        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){  //如果这个用户的status已经是1，说明这是一次重复激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){ //如果传入的激活码和激活码是一样的，说明激活成功
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return  ACTIVATION_FAILURE;
        }
    }

    /**
     * 登陆
     * @param username
     * @param password
     * @param expiredSeconds
     * 正常情况下要处理 username，password，code 3个输入框的参数的，只是这里code 不需要在业务层处理，所以就没写
     * 而这里传入 expiredSeconds 是因为登陆成功需要生成登陆凭证，而登录凭证实体的属性expired需要用expiredSeconds计算出
     * expired是过期的时间，expiredSeconds是从登陆到过期之间的总秒数
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String ,Object> map = new HashMap<>();
        //账户名为空处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        //密码为空处理
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        //验证账号是否存在
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        //验证状态是否激活
        if( user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtils.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        //生成登录凭证(登陆成功就得生成登录凭证，给你发一张“卡”)
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setStatus(0);//0-有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }


    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }
}
