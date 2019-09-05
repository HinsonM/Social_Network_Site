package com.simulationlab.QA_2.dao;

import com.simulationlab.QA_2.model.Comment;
import com.simulationlab.QA_2.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = "from_id, to_id, conversation_id, content, created_date, has_read";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{fromid}, #{toid}, #{conversationId}, #{content}, #{createdDate}, #{hasRead})"})
    int addMessage(Message message);

    // update has_read
    @Update({"update ", TABLE_NAME, " set status = #{status} where to_id = #{userId}"})
    void hasRead(@Param("userId") int userId, @Param("status") int status);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id = #{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where from_id = #{userId} or to_id = #{userId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getMsgsOfUser(@Param("userId") int userId,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    @Select({"select count(id)"," from ", TABLE_NAME, " where conversation_id = #{conversationId} and to_id = #{userId} and has_read = 0"})
    int getUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

}
