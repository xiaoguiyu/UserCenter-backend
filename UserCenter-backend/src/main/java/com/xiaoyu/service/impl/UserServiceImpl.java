package com.xiaoyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;


    /**
     * 给密码加盐
     */
    private static final String SALT = "xiaoyu";





    @Override
    public long register(String userAccount, String userPassword, String checkPassword) {

        // 用户账号 密码  校验密码 的非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }

        // 使用正则来匹配 特殊字符, 如果账号存在特殊字符,
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~ ！ @# ￥ %……&* （）——+|{} 【】‘；： ”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        // 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        // 用户账号是否重复
        if (userMapper.selectCount(userQueryWrapper) > 0) {
            return -1;
        }

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 用户账号 密码  校验密码 的非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }

        // 使用正则来匹配 特殊字符, 如果账号存在特殊字符, 返回null
        String validPattern = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~ ！ @# ￥ %……&* （）——+|{} 【】‘；： ”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
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
            return null;
        }

        User safetyUser = getSafetyUser(user);
        // 保存session
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 对用户信息进行脱敏
     * @param originUser 原始的user
     * @return 脱敏后的user
     */
    @Override
    public User getSafetyUser(User originUser) {

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
        return safetyUser;
    }





}




