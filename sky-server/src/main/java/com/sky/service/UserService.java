package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

import javax.security.auth.login.LoginException;

public interface UserService {
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO) throws LoginException;
}
