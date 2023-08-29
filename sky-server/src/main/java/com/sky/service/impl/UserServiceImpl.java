package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) throws LoginException {
        //利用HTTPClient发送请求，主要获取openId
        String openid = getOpenid(userLoginDTO.getCode());
        //为空代表登录失败
        if (openid == null) {
            throw new LoginException(MessageConstant.LOGIN_FAILED);
        }

        //不为空，判断用户是否为新用户
        User user = userMapper.getByOpenId(openid);
        //新用户则进行注册，即新增
        if (user == null) {
            //存储openid以及创建时间即可
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }
        //用户存在且已注册
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        //请求微信服务，获取微信服务返回的JSON
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        //将JSON转换为对象
        JSONObject jsonObject = JSONObject.parseObject(json);
        //获取JSON中的openid
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
