package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.model.HostHolder;
import com.simulationlab.QA_2.model.Question;
import com.simulationlab.QA_2.model.ViewObject;
import com.simulationlab.QA_2.service.QuestionService;
import com.simulationlab.QA_2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questions = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for(Question question:questions) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            // 此处数据库若没有，则vo中的user为null,则前端无法渲染，但是一般GET不会捕获异常
            vo.set("current", hostHolder.getUser());
            vos.add(vo);
        }
        return vos;
    }

    // 前端输入Url，然后到这里由controller来

    // 如普通的首页解析，简单的response body
    @RequestMapping(path={"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {
//        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path={"/get"}) //method = {RequestMethod.GET, RequestMethod.POST}
    @ResponseBody
    public List<ViewObject> more(Model model,
                            @RequestParam(value = "offset", defaultValue = "0") int offset,
                            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<ViewObject> vos = getQuestions(0, 0, 10);
        return vos;
    }

    @RequestMapping(path={"/user/{userId}"}, method = {RequestMethod.GET})
    public String userIndex(Model model,
                            @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }
}
