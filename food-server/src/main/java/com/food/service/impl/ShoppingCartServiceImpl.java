package com.food.service.impl;

import com.food.context.BaseContext;
import com.food.dto.ShoppingCartDTO;
import com.food.entity.Dish;
import com.food.entity.Setmeal;
import com.food.entity.ShoppingCart;
import com.food.mapper.DishMapper;
import com.food.mapper.SetmealMapper;
import com.food.mapper.ShoppingCartMapper;
import com.food.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 首先判断该用户下购物车中是否有相同菜品或套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0){
            // 购物车中有该菜品或套餐，则将该菜品或商品的数量加一
            ShoppingCart shoppingCartItem = list.get(0);
            shoppingCartItem.setNumber(shoppingCartItem.getNumber() + 1);
            shoppingCartMapper.updateNumber(shoppingCartItem);
        }else {
            // 购物车中没有该菜品或套餐
            // 判断你要添加的时菜品还是套餐
            if (shoppingCartDTO.getDishId() != null){
                // 你要添加的是菜品,查询该菜品的信息,并将该菜品的信息赋给购物车的实体类
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                // 你要添加的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            // 设置公共字段
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 添加到购物车中
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查询该用户下的购物车的所有信息
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        // 查询该用户下的购物车的所有信息
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
    }
    /**
     *删除购物车中一个商品
     */
    @Override
    public void deleteOneCart(ShoppingCartDTO shoppingCartDTO) {
        // 首先获取到该商品的信息（即数量）
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 获取到你要删除的商品的信息
        shoppingCart = shoppingCartMapper.list(shoppingCart).get(0);
        // 获取该商品的数量
        Integer number = shoppingCart.getNumber();
        if (number > 1){
            // 执行修改操作，将数量减一
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateNumber(shoppingCart);
        }else {
            shoppingCartMapper.delete(shoppingCart);
        }
    }
}
