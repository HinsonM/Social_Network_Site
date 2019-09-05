package com.simulationlab.QA_2.interceptor;

import com.simulationlab.QA_2.dao.LoginTicketDAO;
import com.simulationlab.QA_2.dao.UserDAO;
import com.simulationlab.QA_2.model.HostHolder;
import com.simulationlab.QA_2.model.LoginTicket;
import com.simulationlab.QA_2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.UserDataHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    // 接受请求后，进controller前
    // 每次向服务端发起请求时，验证有无ticket，或者其ticket是否有效，若有效则不需要重新分配，不需要重新登录
    // 同时放进网站当前响应进程context中，如hostHolder.setUser(user)
    // 检验是否有效当然要访问数据库查询是否有啦


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                 if (cookie.getName().equals("ticket")) {
                     ticket = cookie.getValue();
                     break;
                 }
            }
        }

        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            // null；过期；失效等
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() == 1) {
                return true;
            }
            hostHolder.setUser(userDAO.selectById(loginTicket.getUserId()));
            Date recentLoginTime = new Date();
            int id = loginTicket.getUserId();
            userDAO.updateLoginTime(recentLoginTime, id);
            hostHolder.getUser().setRecentLoginTime(recentLoginTime);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
