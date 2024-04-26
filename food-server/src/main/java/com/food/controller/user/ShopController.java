package com.food.controller.user;

import com.food.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端和用户端都有ShopController，当springboot通过类名及逆行ioc注入时就会发生冲突
 * 所以我们需要将这两个ShopController的名字区分开来，使用RestController中的value即可
 */
@RestController("userShopController")
@RequestMapping("user/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {
    public static final String Key = "SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer  shopStatus = (Integer) valueOperations.get(Key);
        return Result.success(shopStatus);
    }
}
