package com.food.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.food.constant.MessageConstant;
import com.food.dto.UserLoginDTO;
import com.food.entity.User;
import com.food.exception.LoginFailedException;
import com.food.mapper.UserMapper;
import com.food.properties.WeChatProperties;
import com.food.service.UserService;
import com.food.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        // 调用微信接口服务，获得小程序的唯一标识符openid
        String openid = getOpenid(userLoginDTO.getCode());
        // 若openid为空，则登录失败
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断是否为第一次登录，若为第一次登录，则将该用户信息插入到用户表中
        User user = userMapper.getUserByOpenid(openid);
        if (user == null){
            // 为首次登录 ，将该用户插入到用户表中，要回显主键，因为后面需要用到该主键生成token
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }
        // 不是第一次登录，则返回用户信息
        return user;
    }

    /**
     * 调用微信接口服务，获得小程序的唯一标识符openid
     * @param code
     * @return
     */
    public String getOpenid(String code){
        // 调用微信接口服务，获得小程序的唯一标识符openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        // 将json转换为json对象，并获得里面的openid
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
