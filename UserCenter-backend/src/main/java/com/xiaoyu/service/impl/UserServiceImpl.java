package com.xiaoyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyu.common.ErrorCode;
import com.xiaoyu.exception.CustomException;
import com.xiaoyu.model.domain.User;
import com.xiaoyu.service.UserService;
import com.xiaoyu.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xiaoyu.constant.UserConstant.USER_LOGIN_STATE;


/**
* @author xiaoyu
* @date 2023/03/06 19:03
* @version 1.0
*/

@Slf4j
@Service
@SuppressWarnings("all")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;


    /**
     * 给密码加盐
     */
    private static final String SALT = "xiaoyu";


    @Override
    public long register(String userAccount, String userPassword, String checkPassword, String planetCode) {

        // 用户账号 密码  校验密码 的非空判断
        // todo 封装全局的异常处理类
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new CustomException(ErrorCode.NULL_ERROR, "用户的参数为空");
        }
        if (userAccount.length() < 4) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户账号小于4位!");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户密码太短");
        }
        if (planetCode.length() > 5) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "星球编号过长!");
        }

        // 使用正则来匹配 特殊字符, 如果账号存在特殊字符,
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~ ！ @# ￥ %……&* （）——+|{} 【】‘；： ”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户账号存在特殊字符");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户密码两次不一致");
        }

        // 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        // 用户账号是否重复
        if (userMapper.selectCount(userQueryWrapper) > 0 ) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户账号重复");
        }

        // 用户的星球编号是否重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planetCode", planetCode);
        // 星球编号是否重复
        if (userMapper.selectCount(userQueryWrapper) > 0 ) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户星球编号重复!");
        }


        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean result = this.save(user);
        if (!result) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "插入数据失败!");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 用户账号 密码  校验密码 的非空判断
        // todo 封装全局的状态信息码
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new CustomException(ErrorCode.NULL_ERROR, "用户的参数为空");
        }
        if (userAccount.length() < 4) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户账号小于4位");
        }
        if (userPassword.length() < 8) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户密码小于8位!");
        }

        // 使用正则来匹配 特殊字符, 如果账号存在特殊字符, 返回null
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~ ！ @# ￥ %……&* （）——+|{} 【】‘；： ”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户账号存在特殊符号");
        }

        // 对用户输入的密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 判断用户输入的密码是否正确, 使用加密后的密码和数据库中的密文密码进行匹配
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info(" user login failed, userAccount  cannot match userPassword ! ");
            throw new CustomException(ErrorCode.PARAMS_ERROR, "账号和密码不匹配!");
        }

        User safetyUser = getSafetyUser(user);
        // 保存session
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {

        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 对用户信息进行脱敏
     * @param originUser 原始的user
     * @return 脱敏后的user
     */
    @Override
    public User getSafetyUser(User originUser) {

        if (originUser == null) {
            throw new CustomException(ErrorCode.NULL_ERROR, "user为null");
        }

        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        return safetyUser;
    }

}




