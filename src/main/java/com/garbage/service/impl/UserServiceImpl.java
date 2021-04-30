package com.garbage.service.impl;


import com.garbage.common.Const;
import com.garbage.common.ResponseCode;
import com.garbage.common.ServerResponse;
import com.garbage.dao.QQUserMapper;
import com.garbage.dao.UserMapper;
import com.garbage.pojo.QQUser;
import com.garbage.pojo.User;
import com.garbage.service.IUserService;
import com.garbage.util.JWTUtil;
import com.garbage.util.MD5Util;
import com.garbage.util.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private QQUserMapper qqUserMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ServerResponse login(String phoneNumber, String password) throws Exception {
        if (checkVaild(phoneNumber, Const.PHONE).isSuccess()) {
            return ServerResponse.createByErrorMsg("账号不存在");
        }
        String MD5PassWord = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectUser(phoneNumber, MD5PassWord);
        logger.info("登录获取到的用户信息{}", user);
        return ServerResponse.createBySuccess(user);
    }


    @Override
    public ServerResponse qqLogin(String qqId) throws Exception {
        QQUser qqUser = qqUserMapper.selectByQQId(qqId);
        if (qqUser == null) {
            return ServerResponse.createByCodeErrorMsg(ResponseCode.NEED_PHONE.getCode(), "qq号为空，参数错误");
        } else {
            User user = userMapper.selectByPhone(qqUser.getPhone());
            return addToken(user);
        }
    }

    private ServerResponse addToken(User user) {
        if (user != null) {
            String img = user.getImg();
            if (img.startsWith(Const.QQ_IMG_PREFIX)) {
                img = img.substring(Const.QQ_IMG_PREFIX.length());
            }
            user.setImg(img);
            String token = null;
            try {
                token = JWTUtil.createToken(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map map = new HashMap(4);
            map.put("user", user);
            map.put("token", token);
            return ServerResponse.createBySuccess("登录成功", map);
        } else {
            return ServerResponse.createByErrorMsg("登录失败");
        }
    }

    public boolean checkValid(User user) {
        if (StringUtils.isBlank(user.getPhone()) || StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPhone())) {
            return false;
        }
        return true;
    }

    @Override
    public ServerResponse register(User user, String msgCode) {
        ServerResponse serverResponse = checkVaild(user.getPhone(), Const.PHONE);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        if (!checkValid(user)) {
            ServerResponse.createByErrorMsg("信息不完全");
        }

        User insertUser = new User();
        insertUser.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        insertUser.setName("手机用户" + user.getPhone());
        insertUser.setPhone(user.getPhone());
        userMapper.insertSelective(insertUser);

        return ServerResponse.createByErrorMsg("注册成功");

    }


    public ServerResponse checkVaild(String str, String type) {
        if (StringUtils.equals(type, Const.PHONE)) {
            if (userMapper.selectPhoneCount(str) > 0) {
                return ServerResponse.createByErrorMsg("手机号码已存在");
            }
        } else {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccessMsg("信息无重复, 可用");
    }

    @Override
    public ServerResponse updateUserInfo(User user) {
        User updateUser = new User();
        if (user.getSex() != null) {
            if (Const.checkSex(user.getSex())) {
                updateUser.setSex(user.getSex());
            }
        }
        if (!user.getStage().isEmpty()) {
            updateUser.setStage(user.getStage());
        }
        if (user.getId() >= 0) {
            updateUser.setId(user.getId());
        }
        if (!user.getName().isEmpty()) {
            updateUser.setName(user.getName());
        }
        if (!user.getMsg().isEmpty()) {
            updateUser.setMsg(user.getMsg());
        }
        if (userMapper.updateByPrimaryKeySelective(updateUser) > 0) {
            return ServerResponse.createBySuccessMsg("更新信息成功");
        }
        return ServerResponse.createByErrorMsg("更新失败");
    }

    @Override
    public ServerResponse loginResetPassword(int id, String password) {
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.MD5EncodeUtf8(password));
        if (userMapper.updateByPrimaryKeySelective(updateUser) > 0) {
            return ServerResponse.createBySuccessMsg("重置成功, 请重新登录");
        }

        return ServerResponse.createBySuccessMsg("重置失败");
    }

    @Override
    public ServerResponse forgetResetPassword(String msgCode, String phoneNumber, String password) {
        if (!PhoneUtil.judgeCodeIsTrue(msgCode, phoneNumber)) {
            return ServerResponse.createByErrorMsg("验证码错误");
        }
        password = MD5Util.MD5EncodeUtf8(password);
        if (userMapper.updateByPhone(phoneNumber, password) > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }

    @Override
    public ServerResponse<User> getUserInfo(Integer userId) {
        try {

            return ServerResponse.createBySuccess(userMapper.selectByPrimaryKey(userId));
        } catch (Exception e) {
            logger.error("获取用户信息出错{}", e.getMessage());
            return ServerResponse.createByError("找不到此用户信息");
        }
    }


    @Override
    public ServerResponse<User> checkName(String userName) {
        if (userMapper.selectNameCount(userName) > 0) {
            return ServerResponse.createByErrorMsg("昵称重复");
        }
        return ServerResponse.createBySuccessMsg("昵称可用");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServerResponse qqRegister(String qqId, String phone, String msgCode, String img, String name) {
        ServerResponse serverResponse;
        if (!(serverResponse = checkVaild(phone, Const.PHONE)).isSuccess()) {
            return serverResponse;
        }
        if (!PhoneUtil.judgeCodeIsTrue(msgCode, phone)) {
            return ServerResponse.createByErrorMsg("验证码错误");
        }
        QQUser qqUser = new QQUser();
        qqUser.setQqId(qqId);
        qqUser.setPhone(phone);
        if (qqUserMapper.insertSelective(qqUser) > 0) {
            User insetUser = new User();
            insetUser.setPhone(phone);
            insetUser.setPassword(qqId);
            insetUser.setImg(Const.QQ_IMG_PREFIX + img);
            insetUser.setName(name);
            if (userMapper.insertSelective(insetUser) > 0) {
                User user = userMapper.selectByPhone(phone);
                return ServerResponse.createBySuccess("验证成功", addToken(user));
            }
        }
        return ServerResponse.createByErrorMsg("验证失败");
    }
}
