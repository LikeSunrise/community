package com.newcoder.community.dao;

import com.newcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CommentMapper {


    @Select("select * from comment where status = 0 and entity_type = #{entityType} and entity_id = #{entityId} order by create_time asc limit #{offset},#{limit}")
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    @Select("select count(id) from comment where status = 0 and entity_type = #{entityType} and entity_id = #{entityId}")
    int selectCountByEntity(int entityType, int entityId);
}
