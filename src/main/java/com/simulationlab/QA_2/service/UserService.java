package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.dao.LoginTicketDAO;
import com.simulationlab.QA_2.dao.UserDAO;
import com.simulationlab.QA_2.model.LoginTicket;
import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.util.QaUtil;
import freemarker.template.utility.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String name, String password) {
        Map<String, Object> msg = new HashMap<String, Object>();
        if(StringUtils.isEmpty(name)) {
            msg.put("msg", "用户名不能为空");
            return msg;
        }

        if(StringUtils.isEmpty(password)) {
            msg.put("msg", "密码不能为空");
            return msg;
        }

        User user =userDAO.selectByName(name);
        if(user != null) {
            msg.put("msg", "用户已被注册");
            return msg;
        }

        // add user
        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(QaUtil.MD5(password+user.getSalt()));
        user.setRecentLoginTime(new Date());
        userDAO.addUser(user);

        // add LoginTicket
        String ticket = addLoginTicket(user.getId());
        msg.put("ticket", ticket);
        msg.put("userId", user.getId());
        return msg;
    }


    public Map<String, Object> login(String name, String password) {
        Map<String, Object> msg = new HashMap<String, Object>();
        if(StringUtils.isEmpty(name)) {
            msg.put("msg", "用户名不能为空");
            return msg;
        }

        if(StringUtils.isEmpty(password)) {
            msg.put("msg", "密码不能为空");
            return msg;
        }

        User user =userDAO.selectByName(name);
        if(user == null) {
            msg.put("msg", "用户不存在");
            return msg;
        }

        if(!user.getPassword().equals(QaUtil.MD5(password+user.getSalt()))) {
            msg.put("msg", "密码不正确");
            return msg;
        }

        // add LoginTicket
        String ticket = addLoginTicket(user.getId());
        userDAO.updateLoginTime(new Date(), user.getId());
        msg.put("ticket", ticket);
        msg.put("userId", user.getId());
        return msg;
    }

    public String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-", ""));
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        loginTicket.setExpired(date);
        loginTicketDAO.addLoginTicket(loginTicket);
        return loginTicket.getTicket();

    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}