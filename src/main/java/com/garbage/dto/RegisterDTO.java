package com.garbage.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    // 手机号
    String iphone;

    // 密码
    String password;

    // 短信验证码
    String msgCode;
}
