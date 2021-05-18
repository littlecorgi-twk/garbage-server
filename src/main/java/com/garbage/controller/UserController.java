package com.garbage.controller;

import com.garbage.common.Const;
import com.garbage.common.ServerResponse;
import com.garbage.dao.UserMapper;
import com.garbage.dto.GarbageCollectDTO;
import com.garbage.dto.PhoneAndPasswordDTO;
import com.garbage.dto.RegisterDTO;
import com.garbage.pojo.GarbageCollect;
import com.garbage.pojo.User;
import com.garbage.service.IFileService;
import com.garbage.service.IGarbageCollectService;
import com.garbage.service.IUserService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
@Slf4j
@Controller
@RequestMapping("/user/")
public class UserController {

    @Resource
    private IUserService iUserService;

    @Resource
    private IGarbageCollectService iGarbageCollectService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IFileService iFileService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String MESSAGE_CODE = "67673";


    @ApiOperation(value = "登录")
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(
            HttpSession session, // Spring自动添加
            @ApiParam(value = "手机号和密码", example = "") @RequestBody PhoneAndPasswordDTO phoneAndPassword
    ) {
        ServerResponse serverResponse;
        try {
            serverResponse = iUserService
                    .login(phoneAndPassword.getPhoneNumber(), phoneAndPassword.getPassword());
        } catch (Exception e) {
            logger.error(e.getMessage());
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
            @ApiParam(value = "图像文件") @RequestBody MultipartFile file,
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
    public ServerResponse getMsgcode(@ApiParam(value = "手机号") @RequestParam String phoneNumber) {
        // dubug模式，因为bmob短信服务到期，所以采用此方式
        return ServerResponse.createBySuccessMsg("发送成功，短信验证码是：" + MESSAGE_CODE);

        // if (PhoneUtil.getVerificationCode(phoneNumber) != null) {
        //     return ServerResponse.createBySuccessMsg("发送成功");
        // }
        // return ServerResponse.createByErrorMsg("发送失败");
    }

    @ApiOperation(value = "重置密码（通过session修改）")
    @RequestMapping(value = "loginresetpassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginResetPassword(
            HttpSession session, // Spring自动添加
            @ApiParam(value = "密码", example = "") @RequestBody String password
    ) {
        ServerResponse serverResponse;
        Integer userId = (Integer) session.getAttribute(Const.ID);
        if ((serverResponse = iUserService.loginResetPassword(userId, password)).isSuccess()) {
            return serverResponse;
        } else {
            return ServerResponse.createByErrorMsg("重置失败");
        }
    }

    @ApiOperation(value = "忘记密码（通过手机号修改）")
    @RequestMapping(value = "resetPasswordWithoutLogin.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPasswordWithoutLogin(
            @ApiParam(value = "手机号") @RequestParam String phone,
            @ApiParam(value = "验证码") @RequestParam String msgCode,
            @ApiParam(value = "新密码") @RequestParam String password
    ) {
        ServerResponse serverResponse = iUserService.forgetResetPassword(msgCode, phone, password);
        return serverResponse;
    }


    @ApiOperation(value = "注册")
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(
            @ApiParam(value = "手机号、密码和短信验证码") @RequestBody RegisterDTO register
    ) {
        User user = new User();
        user.setPhone(register.getIphone());
        user.setPassword(register.getPassword());
        user.setMsg(register.getMsgCode());
        return iUserService.register(user, register.getMsgCode());
    }

    @ApiOperation(value = "验证短信验证码")
    @RequestMapping(value = "check_msg.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkMsg(
            @ApiParam(value = "手机号") @RequestParam String phone,
            @ApiParam(value = "短信验证码") @RequestBody String msgCode
    ) {
        if (!msgCode.equals(MESSAGE_CODE)) {
            return ServerResponse.createByErrorMsg("验证码不正确");
        }
        return ServerResponse.createBySuccessMsg("验证码正确");
        // if (!PhoneUtil.judgeCodeIsTrue(msgCode, phone)) {
        //     return ServerResponse.createByErrorMsg("验证码不正确");
        // }
        // return ServerResponse.createBySuccessMsg("验证码正确");
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
            @ApiParam(value = "用户信息") @RequestBody User user
    ) {
        Integer userId = (Integer) session.getAttribute(Const.ID);
        user.setId(userId);
        return iUserService.updateUserInfo(user);
    }


    @ApiOperation(value = "上传垃圾回收点")
    @RequestMapping(value = "uploadgarbagecollect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse uploadGarbageCollect(
            @ApiParam(value = "经纬度和图片") @RequestBody GarbageCollectDTO garbageCollectDTO
    ) {
        GarbageCollect garbageCollect = new GarbageCollect();
        garbageCollect.setLatitude(garbageCollectDTO.getLatitude());
        garbageCollect.setLongitude(garbageCollectDTO.getLongitude());
        garbageCollect.setImg(garbageCollectDTO.getImg());
        return iGarbageCollectService.uploadGarbageCollect(garbageCollect);
    }

    @ApiOperation(value = "获取垃圾回收点")
    @RequestMapping(value = "getgarbagecollect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getGarbageCollect(
            @ApiParam(value = "纬度") @RequestParam double latitude,
            @ApiParam(value = "经度") @RequestParam double longitude
    ) {
        return iGarbageCollectService.getGarbageCollect(latitude, longitude);
    }

    @ApiOperation(value = "QQ登录")
    @RequestMapping(value = "qqlogin.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse qqLogin(
            @ApiParam(value = "qqId") @RequestBody String qqId
    ) {
        try {
            return iUserService.qqLogin(qqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMsg("登录失败");
    }

}
