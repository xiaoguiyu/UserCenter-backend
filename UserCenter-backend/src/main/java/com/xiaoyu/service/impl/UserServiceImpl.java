package com.xiaoyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyu.model.domain.User;
import com.xiaoyu.service.UserService;
import com.xiaoyu.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
* @author xiaoyu
* @date 2023/03/06 19:03
* @version 1.0
*/

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;

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

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        // 用户账号是否重复
        if (userMapper.selectCount(userQueryWrapper) > 0) {
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
        final String SALT = "xiaoyu";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

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
}




