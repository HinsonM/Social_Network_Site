package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.dao.CommentDAO;
import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.util.QaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    // 发一条评论并过滤非法字符及敏感词
    public int addComment(Comment comment) {
        comment.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(comment.getContent())));
        return commentDAO.addComment(comment);
    }

    // 删除一条评论
    //在数据库中并没有真正地去删除记录，而是更新其标志。可能待会有什么算法来进行垃圾回收
    public void deleteComment(int entityId, int entityType) {
        commentDAO.updateStatus(entityId, entityType, 1);
    }

    // 获取实体下的评论数
    public int getCommentCount(int entityId, int entityType) {
         return commentDAO.getCommentCount(entityId, entityType);
    }


    // 获取实体下的所有评论
    public List<Comment> getCommentByEntity(int entityId, int entityType) {
        return commentDAO.selectByEntity(entityId, entityType);
    }

    // 获取用户所发表的所有评论的数目
    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }


    public Comment getCommentById(int id) {
        return commentDAO.selectById(id);
    }
}