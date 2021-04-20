package com.garbage.dao;

import com.garbage.pojo.QQUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QQUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(QQUser record);

    int insertSelective(QQUser record);

    QQUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(QQUser record);

    int updateByPrimaryKey(QQUser record);

    QQUser selectByQQId(String QQId);
}