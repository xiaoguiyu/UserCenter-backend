package com.xiaoyu.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/7 13:20
 */

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = 5326191581975918591L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;



}
