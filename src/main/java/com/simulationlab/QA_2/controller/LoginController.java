package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.async.EventModel;
import com.simulationlab.QA_2.async.EventProducer;
import com.simulationlab.QA_2.async.EventType;
import com.simulationlab.QA_2.model.EntityType;
import com.simulationlab.QA_2.service.UserService;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;
import java.util.Date;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    // register ??? 疑问：这里为什么是/reg/，而非/reg
    @RequestMapping(path={"/reg/"}, method = {RequestMethod.POST})
    public String register(Model model,
                            @RequestParam("name") String name,
                            @RequestParam("password") String password,
                            @RequestParam(value = "next", required = false) String next,
                            @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                            HttpServletResponse response) {
        try {
            // 注册账户
            Map<String, Object> map = userService.register(name, password);
            if (map.containsKey("ticket")) { // 注册成功：下发一个cookie，check rememberme, check next, 否则返回首页
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString()); // 该cookie是一个键值对，记录服务器发给客户端的一个token，记录会话信息，例如购物场景中，当结算时，买了一些产品，若无cookie，服务器并不知道客户端买了什么
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600*24*5); // 设置记住的最大天数
                }
                response.addCookie(cookie);
                // 设定最近登录时间，用于判断活跃度，据此来决定timeline的推模式还是拉模式
                userService.getUser((int)map.get("userId")).setRecentLoginTime(new Date());
                eventProducer.fireEvent(new EventModel().
                        setType(EventType.LOGIN).
                        setActorId((int)map.get("userId")).
                        setExt("username", name).
                        setExt("date", new Date().toString()).
                        setActorId(QaUtil.SYSTEM_ID).
                        setEntityOwnerId((int)map.get("userId")));

                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next; // 重定向到刚才页面
                }
                return "redirect:/"; // 登陆成功，且不返回刚才页面，则直接重定向到首页
            } else { // 注册失败，同时返回错误信息，返回login页面继续注册，
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }

    // login
    @RequestMapping(path={"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("name") String name,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(name, password);
            if (map.containsKey("ticket")) { // 登录成功：下发一个cookie，check rememberme, check next, 否则返回首页
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString()); // 该cookie是一个键值对，记录服务器发给客户端的一个token，记录会话信息，例如购物场景中，当结算时，买了一些产品，若无cookie，服务器并不知道客户端买了什么
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600*24*5); // 记住5天
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel().
                        setType(EventType.LOGIN).
                        setActorId((int)map.get("userId")).
                        setExt("username", name).
                        setExt("date", new Date().toString()).
                        setActorId(QaUtil.SYSTEM_ID).
                        setEntityOwnerId((int)map.get("userId")));

                // 是否需要重定向到之前的需要登录到的页面
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next; // 重定向到刚才页面
                }
                return "redirect:/"; // 登陆成功，且不返回刚才页面，则直接重定向到首页
            } else { // 登录失败，同时返回错误信息，返回login页面继续注册，
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            model.addAttribute("msg", "登陆异常");
            return "login";
        }
    }

    // 实际上在Index中当点击“登录注册”时，便会发出一个/relogin请求，
    // relogin
    @RequestMapping(path={"/reglogin"}, method = {RequestMethod.GET})
    public String relogin(Model model,
                          @RequestParam(value = "next",required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    // logout
    //???为什么这里既有GET，也有POST,logout嘛，更改服务器状态，将数据库中对应的ticket status更改为1
    @RequestMapping(path={"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(Model model,
                         @CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }


}
