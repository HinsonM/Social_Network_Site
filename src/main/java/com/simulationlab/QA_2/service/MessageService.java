package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.dao.CommentDAO;
import com.simulationlab.QA_2.dao.MessageDAO;
import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    // 发一条站内信并过滤非法字符及敏感词
    public int addMessage(Message message) {
        message.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(message.getContent())));
        return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
    }

    // 站内信标为已读
    public void hasReadMessgae(int userId) {
        messageDAO.hasRead(userId,1);
    }

    // 获取会话中所有message
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    // 获取用户所有的message
    public List<Message> getMsgsOfUser(int userId, int offset, int limit) {
        return messageDAO.getMsgsOfUser(userId, offset, limit);
    }

    // 获取用户某个会话中的未读信息数量
    public int getUnReadCount(int userId, String conversationId) {
        return messageDAO.getUnReadCount(userId, conversationId);
    }
}
