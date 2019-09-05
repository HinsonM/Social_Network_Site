package com.simulationlab.QA_2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Qa2Application.class)	//??? 不是很懂这
@Sql("/init-schema.sql")    // 每次跑测试用例时均执行init-schema.sql中的sql语句
public class Qa2ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
