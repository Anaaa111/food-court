package com.food.controller.user;

import com.food.result.Result;
import com.food.service.DishService;
import com.food.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDishController")
@RequestMapping("user/dish")
@Slf4j
@Api(tags = "用户端菜品相关接口")
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品以及对应的口味
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品以及对应的口味")
    // Cacheable注解会在执行这个方法之前，查询数据库中是否会有dish::categoryId缓存，
    // 若有直接取出来然后返回，若没有则执行该方法，然后将该方法的返回结果存储到缓存中
    @Cacheable(cacheNames = "dish", key = "#categoryId")
    public Result<List<DishVO>> getDishListByCategory(Long categoryId){
        /**
         * 用于前端会有很多人访问菜品，过多的访问数据库会导致压力
         * 我们可以将菜品缓存到redis中，访问菜品前就可以先去redis查看是否有该菜品，
         * 若有的话则直接返回，就不需要查数据库了
         * 若没有，则查询数据库
         * 注意：在后端，更新，起售或停售菜品时，我们需要将缓存中的菜品删除
         * 注意：添加和删除菜品时不需要更新菜品，因为添加菜品时，菜品默认时停售状态，不需要显示在前端，删除菜品时，需要先将菜品停售才可以删除
         */
        // String key = "dish_" + categoryId;
        // List<DishVO> list = (List<DishVO>)redisTemplate.opsForValue().get(key);
        //
        // if (list != null && list.size() > 0){
        //     // 若缓存中有，则直接返回
        //     return Result.success(list);
        // }

        // 缓存中没有，则查询数据库，然后将查到的数据库存到缓存中
        List<DishVO> list = dishService.getDishWithFlavor(categoryId);
        // redisTemplate.opsForValue().set(key, list);
        return Result.success(list);

    }
}
