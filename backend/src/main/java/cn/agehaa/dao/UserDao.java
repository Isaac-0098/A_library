package cn.agehaa.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.agehaa.pojo.UserPojo;

@Mapper
public interface UserDao extends BaseMapper<UserPojo> {
} 