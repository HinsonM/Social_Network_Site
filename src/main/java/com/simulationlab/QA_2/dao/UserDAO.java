package com.simulationlab.QA_2.dao;

import com.simulationlab.QA_2.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Date;


// 注意这里是interface，为什么要是interface呢？
@Mapper
public interface UserDAO {
    // 注意空格
    // 定义一些宏
    String TABLE_NAME = "user";
    String INSERT_FIELDS = "name, password, salt, head_url, recent_login_time";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    // #{headUrl} 这里读取的是User model中的字段，是驼峰风格
    // 而在sql中是下划线风格
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{name}, #{password}, #{salt}, #{headUrl}, #{recentLoginTime})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where name = #{name}"})
    User selectByName(String name);

    // ???这里直接能够直接获取user的私有数据成员password来赋值吗？
    @Update({"update ", TABLE_NAME, " set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Update({"update ", TABLE_NAME, " set recent_login_time = #{recentLoginTime} where id = #{id}"})
    void updateLoginTime(@Param("recentLoginTime") Date recentLoginTime, @Param("id") int id);

    @Delete({"delete from ", TABLE_NAME, " where id = #{id}"})
    void deleteById(int id);


}
