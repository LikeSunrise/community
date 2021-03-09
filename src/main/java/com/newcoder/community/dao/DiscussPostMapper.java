package com.newcoder.community.dao;

import com.newcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    @SelectProvider(type = DiscussPostDaoProvider.class, method = "selectDiscussPosts")
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    @SelectProvider(type = DiscussPostDaoProvider.class, method = "selectDiscussPostRows")
    int selectDiscussPostRows(@Param("userId") int userId);

    @Insert("insert into discuss_post(id,user_id,title,content,type,status,create_time,comment_count,score) values(#{id},#{userId},#{title},#{content},#{type},#{status},#{createTime},#{commentCount},#{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertDiscussPost(DiscussPost discussPost);


    @Select("select id,user_id,title,content,type,status,create_time,comment_count,score from discuss_post where id = #{id}")
    DiscussPost selectDiscussPostById(int id);

    //    自定义动态sql
    class DiscussPostDaoProvider {
        //查询某用户（user_id指定）发表的所有discuss_post内容
        public String selectDiscussPosts(int userId, int offset, int limit) {
            String sql = "select id,user_id,title,content,type,status,create_time,comment_count,score from discuss_post where status !=2";
//            限制条件：where status!= 2 and userId!=0 and user_id = #{userId}
            if (userId != 0) {
                sql += " and user_id = #{userId}"; //我吐了，这里不加空格肯定错了啊
            }
            sql += " order by type desc,create_time desc limit #{offset},#{limit}"; //从offset开始，查limit条数
            return sql;
        }

        //获取discuss_post表总记录条数
        public String selectDiscussPostRows(int userId) {
            String sql = "select count(id) from discuss_post where status !=2";
//            限制条件：where status!= 2 and userId!=0 and user_id = #{userId}
            if (userId != 0) {
                sql += " and user_id = #{userId}";
            }
            return sql;
        }
    }
}

/*
正确的sql：多条件排序order by、limit限制条数（必须放最后）
SELECT * FROM `user` where status != 2 and type != 6 order by type desc,id desc limit 0,9
 */