package com.food.controller.admin;

import com.food.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {
    /**
     * 由于营业状态要么0要么1，且只有一个字段，单独建一张表得不偿失，
     * 所以使用redis存储营业状态
     */
    public static final String Key = "SHOP_STATUS";

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status){
        // redis对于字符串的操作对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(Key, status);
        return Result.success();
    }
    @GetMapping("status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer  shopStatus = (Integer) valueOperations.get(Key);
        return Result.success(shopStatus);
    }
}
