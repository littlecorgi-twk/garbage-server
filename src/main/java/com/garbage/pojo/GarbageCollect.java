package com.garbage.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GarbageCollect {
    Integer id;

    // 纬度
    Double latitude;

    // 经度
    Double longitude;

    // 图片
    String img;
}
