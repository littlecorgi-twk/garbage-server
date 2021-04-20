package com.garbage.controller;


import com.garbage.common.Const;
import com.garbage.common.ServerResponse;
import com.garbage.dao.UserMapper;
import com.garbage.pojo.User;
import com.garbage.service.IFileService;
import com.garbage.service.IUserService;
import com.garbage.util.PhoneUtil;
import com.garbage.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Resource
    private IUserService iUserService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IFileService iFileService;



    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(HttpSession session, String phoneNumber, String password) {

        ServerResponse serverResponse;
        try {
            serverResponse = iUserService.login(phoneNumber, password);
        } catch (Exception e) {
            return ServerResponse.createByErrorMsg("登录失败");
        }
        User user= (User) serverResponse.getData();
        if(user!=null) {
            session.setAttribute(Const.ID, user.getId());

            return serverResponse;
        }else{
            return ServerResponse.createByErrorMsg("用户名或者密码错误");
        }
    }


    @RequestMapping(value = "need_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse needLogin() {
        return ServerResponse.createByErrorMsg("用户没有登录");
    }

    /**
     * 上传头像
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
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




    @RequestMapping(value = "getmsgcode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getMsgcode(String phoneNumber) {
        if (PhoneUtil.getVerificationCode(phoneNumber) != null) {
            return ServerResponse.createBySuccessMsg("发送成功");
        }
        return ServerResponse.createByErrorMsg("发送失败");
    }

    @RequestMapping(value = "loginresetpassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginResetPassword(HttpSession session, String password) {
        ServerResponse serverResponse;
        Integer userId = (Integer) session.getAttribute(Const.ID);
        if ((serverResponse = iUserService.loginResetPassword(userId, password)).isSuccess())
        {
            return serverResponse;
        }
        else{
            return ServerResponse.createByErrorMsg("重置失败");
        }
    }


    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(String iphone,String password, String msgCode) {
        User user=new User();
        user.setPhone(iphone);
        user.setPassword(password);
        user.setMsg(msgCode);
        return iUserService.register(user, msgCode);
    }


    @RequestMapping(value = "check_msg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkMsg(String phone, String msgCode) {
        if (!PhoneUtil.judgeCodeIsTrue(msgCode, phone)) {
            return ServerResponse.createByErrorMsg("验证码不正确");
        }
        return ServerResponse.createBySuccessMsg("验证码正确");
    }


    @RequestMapping(value = "getmsg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(Const.ID);
        System.out.println(userId);
        return iUserService.getUserInfo(userId);
    }

    @RequestMapping(value = "updatemsg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session, User user) {
        Integer userId = (Integer) session.getAttribute(Const.ID);
        user.setId(userId);
        return iUserService.updateUserInfo(user);
    }



    @RequestMapping(value = "qqlogin.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse qqLogin(String qqId) {
        try {
            return iUserService.qqLogin(qqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMsg("登录失败");
    }

}
