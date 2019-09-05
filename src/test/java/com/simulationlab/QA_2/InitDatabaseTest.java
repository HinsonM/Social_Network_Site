package com.simulationlab.QA_2;

import com.simulationlab.QA_2.dao.CommentDAO;
import com.simulationlab.QA_2.dao.QuestionDAO;
import com.simulationlab.QA_2.dao.UserDAO;
import com.simulationlab.QA_2.model.Question;
import com.simulationlab.QA_2.model.User;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.UserDataHandler;

import java.util.Date;
import java.util.Random;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Qa2Application.class)	//??? 不是很懂这
@Sql("/init-schema.sql")    // 每次跑测试用例时均执行init-schema.sql中的sql语句
public class InitDatabaseTest {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	CommentDAO commentDAO;

	@Test
	public void contextLoads() {
//		initdb();
//		resetDbCommentCount();
	}

	private void resetDbCommentCount() {
		for (int i = 0; i < 18; i++) {
			questionDAO.selectById(i).setCommentCount(0);
		}
	}

	private void initdb() {
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("User%d", i));
			user.setPassword("");
			user.setSalt("");
			user.setRecentLoginTime(new Date());
			userDAO.addUser(user);

			user.setPassword("newpassword");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(0);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
			question.setCreatedDate(date);
			question.setUserId(user.getId());
			question.setTitle(String.format("Title{%d}", user.getId()));
			question.setContent(String.format("Content[%d]我们如何处理非碳基生命的伦理问题，" +
					"譬如一天全身金属的拥有强人工智能的机器人出现", user.getId()));
			questionDAO.addQuestion(question);
		}
		Assert.assertEquals("update password failed", "newpassword", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull("deleting failed", userDAO.selectById(1));
		System.out.println("Testing");

		Question question = questionDAO.selectById(11);
		System.out.println(String.format("qustionId: %d", question.getId()));

		User user = userDAO.selectById(11);
		System.out.println(String.format("userId: %d", user.getId()));
	}

}
