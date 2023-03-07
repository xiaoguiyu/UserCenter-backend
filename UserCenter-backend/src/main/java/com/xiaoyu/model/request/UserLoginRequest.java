package com.xiaoyu.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/7 13:21
 */

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 2728850124033566050L;

    private String userAccount;

    private String userPassword;


}
