package com.mrguan.tcyhwapp.mapper;

import com.mrguan.tcyhwapp.entity.vo.UserInfoVO;
import com.mrguan.tcyhwapp.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    @Select("select id,id avatar_id,avatar_url,phone_number mobile,nickname,gender sex,enable status,open_id openid from user_info where id=#{userId}")
    UserInfoVO getUserInfo(Long userId);

    @Select("select *  from user_info where phone_number=#{phone}")
    UserInfo getByMobile(String phone);
    @Select("select *  from user_info where open_id=#{openid}")
    UserInfo getByOpenid(String openid);
}
