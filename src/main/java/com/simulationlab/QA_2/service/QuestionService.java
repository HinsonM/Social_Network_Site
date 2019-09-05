package com.simulationlab.QA_2.service;

import com.simulationlab.QA_2.dao.QuestionDAO;
import com.simulationlab.QA_2.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public Question getById(int id) {
        return questionDAO.selectById(id);
    }

    public int addQuestion(Question question) {
        // sensitive filter
        String title = HtmlUtils.htmlEscape(question.getTitle());
        String content = HtmlUtils.htmlEscape(question.getContent());
        question.setTitle(sensitiveService.filter(title));
        question.setContent(sensitiveService.filter(content));

        // 提交成功, questionDAO.addQuestion 返回大于0
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int commentCount) {
        return questionDAO.updateCommentCount(id, commentCount);
    }
}
