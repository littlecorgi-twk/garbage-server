package com.garbage.controller;

import com.garbage.common.Const;
import com.garbage.common.ServerResponse;
import com.garbage.dao.UserMapper;
import com.garbage.pojo.User;
import com.garbage.service.IFileService;
import com.garbage.service.IUserService;
import com.garbage.util.PhoneUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Api(value = "用户相关操作")
@Controller
@RequestMapping("/user/")
public class UserController {

    @Resource
    private IUserService iUserService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IFileService iFileService;


    @ApiOperation(value = "登录")
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(
            HttpSession session, // Spring自动添加
            @ApiParam(value = "手机号", example = "") String phoneNumber,
            @ApiParam(value = "密码", example = "") String password
    ) {
        ServerResponse serverResponse;
        try {
            serverResponse = iUserService.login(phoneNumber, password);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg("登录失败");
        }
        User user = (User) serverResponse.getData();
        if (user != null) {
            session.setAttribute(Const.ID, user.getId());

            return serverResponse;
        } else {
            return ServerResponse.createByErrorMsg("用户名或者密码错误");
        }
    }

    @ApiOperation(value = "需要登录")
    @RequestMapping(value = "need_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse needLogin() {
        return ServerResponse.createByErrorMsg("用户没有登录");
    }

    /**
     * 上传头像
     *
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "上传头像")
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(
            @ApiParam(value = "图像文件") @RequestParam(value = "upload_file", required = false) MultipartFile file,
            HttpServletRequest request // Spring自动添加
    ) {
        Integer userId = (Integer) request.getAttribute(Const.ID);
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (targetFileName != null) {
            User newUser = new User();
            newUser.setImg(targetFileName);
            newUser.setId(userId);
            userMapper.updateByPrimaryKeySelective(newUser);
        }
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        return ServerResponse.createBySuccess(fileMap);
    }

    @ApiOperation(value = "获取短信验证码")
    @RequestMapping(value = "getmsgcode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getMsgcode(@ApiParam(value = "手机号") String phoneNumber) {
        if (PhoneUtil.getVerificationCode(phoneNumber) != null) {
            return ServerResponse.createBySuccessMsg("发送成功");
        }
        return ServerResponse.createByErrorMsg("发送失败");
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "loginresetpassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginResetPassword(
            HttpSession session, // Spring自动添加
            @ApiParam(value = "密码", example = "") String password
    ) {
        ServerResponse serverResponse;
        Integer userId = (Integer) session.getAttribute(Const.ID);
        if ((serverResponse = iUserService.loginResetPassword(userId, password)).isSuccess()) {
            return serverResponse;
        } else {
            return ServerResponse.createByErrorMsg("重置失败");
        }
    }

    @ApiOperation(value = "注册")
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(
            @ApiParam(value = "手机号") String iphone,
            @ApiParam(value = "密码") String password,
            @ApiParam(value = "验证码") String msgCode
    ) {
        User user = new User();
        user.setPhone(iphone);
        user.setPassword(password);
        user.setMsg(msgCode);
        return iUserService.register(user, msgCode);
    }

    @ApiOperation(value = "验证短信验证码")
    @RequestMapping(value = "check_msg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkMsg(
            @ApiParam(value = "手机号") String phone,
            @ApiParam(value = "短信验证码") String msgCode
    ) {
        if (!PhoneUtil.judgeCodeIsTrue(msgCode, phone)) {
            return ServerResponse.createByErrorMsg("验证码不正确");
        }
        return ServerResponse.createBySuccessMsg("验证码正确");
    }

    @ApiOperation(value = "获取用户信息")
    @RequestMapping(value = "getmsg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(
            HttpSession session // Spring自动添加
    ) {
        Integer userId = (Integer) session.getAttribute(Const.ID);
        System.out.println(userId);
        return iUserService.getUserInfo(userId);
    }

    @ApiOperation(value = "更新用户信息")
    @RequestMapping(value = "updatemsg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(
            HttpSession session, // Spring自动添加
            @ApiParam(value = "用户信息") User user
    ) {
        Integer userId = (Integer) session.getAttribute(Const.ID);
        user.setId(userId);
        return iUserService.updateUserInfo(user);
    }

    @ApiOperation(value = "QQ登录")
    @RequestMapping(value = "qqlogin.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse qqLogin(
            @ApiParam(value = "qqId") String qqId
    ) {
        try {
            return iUserService.qqLogin(qqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMsg("登录失败");
    }

}
