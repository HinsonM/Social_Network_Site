package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.service.QAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired // 不是在方法里面声明，否则出现错误，IOC
            QAService qaService;

    public String sep() {
        return "********************************<br>";
    }

    // URL中path里面的参数的解析 PathVariable:  形如127.0.0.1:8080/profile/admin/1
    // request里面的参数的解析, RequestParam：形如
    @RequestMapping(path={"/profile/{gid}/{uid}"})
    @ResponseBody
    public String profile(@PathVariable("uid") int uid,
                          @PathVariable("gid") String gid,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key", required = false) String key) {
        logger.info("visiting profile");
        return String.format("Profile Page of %s / %d\n" +
                "{%d: %s}", gid, uid, type, key);
    }

    @RequestMapping(path={"/home"}, method = {RequestMethod.GET})
    public String template(Model model) {
        logger.info("visiting home");
        model.addAttribute("hellowords", "I finally came here! It's exciting, isn't it?");
        model.addAttribute("var1", "value1");
        model.addAttribute("var2", "value2");


        List<String> colors = Arrays.asList(new String[]{"RED", "BLUE", "GREEN"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i*i));
        }
        model.addAttribute("map", map);

        User user =  new User("Mo");
        model.addAttribute("user", user);

        return "home";
    }


    @RequestMapping(path={"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                          HttpServletRequest request,
                          HttpSession httpSession
            /*@CookieValue() */) {
        logger.info("visiting request");
        StringBuilder sb = new StringBuilder();
//        sb.append("COOKIEVALUE:" + sessionId)
        Enumeration<String> headerNames = request.getHeaderNames();

        sb.append(sep());
        sb.append(sep());
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        sb.append(sep());
        sb.append(sep());
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                sb.append("Cookie:" + cookie.getName() + " value:" + cookie.getValue() + "<br>");
            }
        }
        sb.append(sep());
        sb.append(sep());
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getPathInfo() + "<br>");
        sb.append(request.getRequestURI() + "<br>");


        response.addHeader("nowcoder", "hello");
        response.addCookie(new Cookie("username", "mochuxuan"));

        return sb.toString();
    }

    @RequestMapping(path={"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession) {
        logger.info("visiting redirect");
        httpSession.setAttribute("msg", "jump from redirect");
        RedirectView red = new RedirectView("/", true);
//        contextRelative: default is off, probably want to almost set it to true
//        off:    Urls starting with "/" are considered relative to the web server root,
//        on:     they are considered relative to the web application root
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY); //??? what does  MOVED_PERMANENTLY mean
        }
        return red;
    }

    @RequestMapping(path={"/admin"}, method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        logger.info("visiting admin");
        if ("admin".equals(key)) {
            return "hello, admin";
        }
        throw new IllegalArgumentException("wrong args");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "你来到了知识的荒原" + e.getMessage();
    }

}



