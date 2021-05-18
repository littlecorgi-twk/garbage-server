package com.garbage.dto;

import lombok.Data;

@Data
public class GarbageCollectDTO {
    // 纬度
    double latitude;

    // 经度
    double longitude;

    // 图片
    String img;
}
