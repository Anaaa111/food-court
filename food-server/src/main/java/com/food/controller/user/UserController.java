package com.food.controller.user;

import com.food.constant.JwtClaimsConstant;
import com.food.dto.UserLoginDTO;
import com.food.entity.User;
import com.food.properties.JwtProperties;
import com.food.result.Result;
import com.food.service.UserService;
import com.food.utils.JwtUtil;
import com.food.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user/user")
@Slf4j
@Api(tags = "用户端的用户相关接口")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    JwtProperties jwtProperties;

    /**
     * 微信登录：
     * 1.前端发送登录请求，并将微信授权码传到后端
     * 2.后端接受微信授权码，并使用授权码调用微信接口获得该用户的唯一标识符openid
     * 3.判断openid是否再数据库中，若不在，就进行自动注册并登录成功，若在则登录成功
     * 4.登录成功后，获取用户id生成token返回给前端
     * @param userLoginDTO
     * @return
     */
    @PostMapping("login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信登录：{}", userLoginDTO.getCode());
        // 微信登录，若登录成功则返回登录的用户信息
        User user = userService.login(userLoginDTO);
        // 生成token，并封装成UserLoginVO返回给前端
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        log.info("登录的用户信息：{}", userLoginVO);
        return Result.success(userLoginVO);
    }
}
