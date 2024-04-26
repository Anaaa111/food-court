package com.food.controller.user;

import com.food.dto.ShoppingCartDTO;
import com.food.entity.ShoppingCart;
import com.food.result.Result;
import com.food.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/shoppingCart")
@Slf4j
@Api(tags = "购物车相关接口")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> getCartList(){
        List<ShoppingCart> shoppingCartList = shoppingCartService.list();
        return Result.success(shoppingCartList);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("clean")
    @ApiOperation("清空购物车")
    public Result cleanCart(){
        shoppingCartService.cleanCart();
        return Result.success();
    }
    /**
     * 删除购物车中一个商品
     */
    @PostMapping("sub")
    @ApiOperation("删除购物车中一个商品")
    public Result deleteOneCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.deleteOneCart(shoppingCartDTO);
        return Result.success();
    }

}
