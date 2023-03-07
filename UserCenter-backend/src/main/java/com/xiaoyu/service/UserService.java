package com.xiaoyu.service;

import com.xiaoyu.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xiaoyu
* @date 2023/03/06 19:03
* @version 1.0
 *
*/


public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户的id
     */
    long register(String userAccount, String userPassword, String checkPassword);
}
