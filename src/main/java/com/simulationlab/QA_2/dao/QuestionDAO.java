package com.simulationlab.QA_2.dao;


import com.simulationlab.QA_2.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

// 为什么要用interface来实现呢
@Mapper
public interface QuestionDAO {
    // Some macro
    String TABLE_NAME = "question";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS; // 有一回id后面漏了个都好，因而取不出来

    // 该方法的sql语句直接在注解中实现，实现较为简单的sql逻辑
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") " +
            "values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    // 该方法的sql语句在xml中实现，可以实现较为复杂的sql逻辑
    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Question selectById(int id);

    // 更新评论数
    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

}
