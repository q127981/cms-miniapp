package com.mrguan.tcyhwapp.mapper;

import com.mrguan.tcyhwapp.entity.vo.CommentVO;
import com.mrguan.tcyhwapp.model.CommentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

@Mapper
public interface CommentInfoMapper extends BaseMapper<CommentInfo> {

    List<CommentVO> getList(@Param("cid") Long cid, @Param("userId") Long userId);

    CommentVO getById(@Param("id") Long id, @Param("userId") Long userId);

    List<CommentVO> getDetailList(@Param("id") Long id, @Param("userId") Long userId);

    @Update("UPDATE comment_info set like_count=like_count+(#{count}) where id=#{id}")
    int addLikeCount(@Param("count") Integer count, @Param("id") Long id);

    @Update("UPDATE comment_info set reply_count=reply_count+(#{count}) where id=#{id}")
    int addReplyCount(@Param("count") Integer count, @Param("id") Long id);
}
