package com.garbage.dao;

import com.garbage.pojo.GarbageCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GarbageCollectMapper {
    GarbageCollect selectById(Integer id);

    int insertSelective(GarbageCollect garbageCollect);

    GarbageCollect selectByLatLongCount(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    List<GarbageCollect> selectByLatLong(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    void delete();
}
