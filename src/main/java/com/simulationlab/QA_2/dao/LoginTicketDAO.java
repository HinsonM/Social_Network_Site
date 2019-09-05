package com.simulationlab.QA_2.dao;

import com.simulationlab.QA_2.model.LoginTicket;
import com.simulationlab.QA_2.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = "user_id, ticket, expired, status";
    String SELECT_FILEDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{userId}, #{ticket}, #{expired}, #{status})"})
    int addLoginTicket(LoginTicket loginTicket);

    @Select({"select ", SELECT_FILEDS, " from ", TABLE_NAME, " where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    // ???传参应该有多种方式，例如如下的@Param("var") type var 又如 type var, 又如Object obj，然后上面通过#{取其属性值}，应该需要定义其参数或者getter和setter
    @Update({"update ", TABLE_NAME, " set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
