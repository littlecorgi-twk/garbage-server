package com.garbage.service;

import com.garbage.common.ServerResponse;
import com.garbage.pojo.GarbageCollect;

public interface IGarbageCollectService {
    ServerResponse uploadGarbageCollect(GarbageCollect garbageCollect);

    ServerResponse getGarbageCollect(double latitude, double longitude);
}
