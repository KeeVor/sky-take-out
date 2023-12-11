package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查用户信息
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 新增用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据时间查找之前总共用户数量
     * @param createTime
     * @return
     */
    Integer queryTotalUserByCreateTimeBefore(LocalDate createTime);

    /**
     * 根据时间查询当天新增用户总量
     * @param createTime
     * @return
     */
    Integer queryTotalUserByCreateTime(LocalDate createTime);
}
