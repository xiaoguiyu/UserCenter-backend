package com.xiaoyu.service;

import com.xiaoyu.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author xiaoyu
* @date 2023/03/06 19:03
* @version 1.0
 *
*/


public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户的id
     */
    long register(String userAccount, String userPassword, String checkPassword, String planetCode);


    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @return 返回注销是否成功的状态码
     */
    int userLogout(HttpServletRequest request);

    /**
     * 对用户信息进行脱敏
     * @param originUser 原始的user
     * @return 脱敏后的user
     */
    User getSafetyUser(User originUser);
}
