package com.food.service;

import com.food.dto.ShoppingCartDTO;
import com.food.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void cleanCart();

    void deleteOneCart(ShoppingCartDTO shoppingCartDTO);
}
