package com.garbage.service.impl;

import com.garbage.common.ServerResponse;
import com.garbage.dao.GarbageCollectMapper;
import com.garbage.pojo.GarbageCollect;
import com.garbage.service.IGarbageCollectService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service("iGarbageCollectService")
public class GarbageCollectServiceImpl implements IGarbageCollectService {
    @Resource
    private GarbageCollectMapper garbageCollectMapper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ServerResponse uploadGarbageCollect(GarbageCollect garbageCollect) {
        if (garbageCollectMapper.selectByLatLongCount(
                garbageCollect.getLatitude(),
                garbageCollect.getLongitude()
        ) == null) {
            garbageCollectMapper.insertSelective(garbageCollect);
        }
        logger.info("上传的垃圾回收点信息{}", garbageCollect);
        return ServerResponse.createBySuccessMsg("上传成功");
    }

    @Override
    public ServerResponse getGarbageCollect(double latitude, double longitude) {
        List<GarbageCollect> garbageCollects =
                garbageCollectMapper.selectByLatLong(latitude, longitude);
        logger.info("获取的垃圾回收点信息{}", garbageCollects);
        return ServerResponse.createBySuccess(garbageCollects);
    }
}
