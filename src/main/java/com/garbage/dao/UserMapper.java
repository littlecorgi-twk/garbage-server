package com.garbage.dao;

import com.garbage.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    String selectNameById(Integer id);

    User selectUser(@Param("phoneNumber") String phoneNumber, @Param("password") String password);

    int selectPhoneCount(String phone);

    int updateByPhone(@Param("phoneNumber") String phoneNumber, @Param("password") String password);

    int selectNameCount(String userName);

    User selectByPhone(String phone);


}