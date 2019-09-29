package com.example.toutiao.service;

import com.example.toutiao.Dao.LoginTicketDAO;
import com.example.toutiao.Dao.UserDAO;
import com.example.toutiao.model.LoginTicket;
import com.example.toutiao.model.User;
import com.example.toutiao.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("password", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user != null){
            map.put("msgname", "用户名已被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png",
                                    new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("password", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user == null){
            map.put("msgname", "用户名不存在");
            return map;
        }
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User getUser(int id){
        return userDAO.selectById(id);

    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }

}
