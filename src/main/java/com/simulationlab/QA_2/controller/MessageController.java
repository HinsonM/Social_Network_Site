package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.dao.UserDAO;
import com.simulationlab.QA_2.model.HostHolder;
import com.simulationlab.QA_2.model.Message;
import com.simulationlab.QA_2.model.User;
import com.simulationlab.QA_2.model.ViewObject;
import com.simulationlab.QA_2.service.MessageService;
import com.simulationlab.QA_2.service.UserService;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    // 用户所有msg
    @RequestMapping(value = {"/msg/list"}, method = {RequestMethod.GET})
    public String getMsgsOfUser(Model model) {
        // 这里已经有拦截器去拦截用户是否为空，所以不需要再判断，未登录则会自动重定向到登录页面
        try {
        int localUserId = hostHolder.getUser().getId();
        List<Message> msgs = messageService.getMsgsOfUser(localUserId, 0, 10);
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for (Message msg : msgs) {
            ViewObject vo = new ViewObject();
            // 站内信的信息：msg，unread, otherSideId, user
            vo.set("message", msg);
            vo.set("unread", messageService.getUnReadCount(localUserId, msg.getConversationId())); // 当前message中对应conversationId的未读数量
            int otherSideId = localUserId == msg.getFromid() ? msg.getToid() : msg.getFromid();
            vo.set("user", userService.getUser(otherSideId));
            vo.set("otherSideId", otherSideId);
            vos.add(vo);
        }
        model.addAttribute("vos", vos);
        } catch(Exception e) {
            logger.error("读取站内信列表失败" + e.getMessage());
        }
        return "message";
    }


    // 某个会话的所有msg
    @RequestMapping(value = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId) {
        // 这里已经有拦截器去拦截用户是否为空，所以不需要再判断，未登录则会自动重定向到登录页面
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> msgs = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> vos = new ArrayList<ViewObject>();
            for (Message msg : msgs) {
                ViewObject vo = new ViewObject();
                // 站内信的信息：msg，sender id, sender url
                User sender = userService.getUser(msg.getFromid());
                if (sender == null) { // 不知道为啥要检验一下
                    continue;
                }
                vo.set("message", msg);
                vo.set("message", msg);
                vo.set("senderId", sender.getId());
                vo.set("senderUrl", sender.getHeadUrl());
                vos.add(vo);
            }
            model.addAttribute("messages", vos);
        } catch(Exception e) {
            logger.error("读取会话失败" + e.getMessage());
        }
        return "messageDetail";
    }

    // 当前用户发给toName 一条信息
    @RequestMapping(path = {"/msg/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("id") int id,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) {
                return "redirect:/reglogin";
            }
            int fromid = hostHolder.getUser().getId();
            Message message = new Message(fromid, id, content, new Date(), 0);
            messageService.addMessage(message);
            return QaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("发送站内信失败" + e.getMessage());
            return QaUtil.getJSONString(1, "error");
        }
    }

}
