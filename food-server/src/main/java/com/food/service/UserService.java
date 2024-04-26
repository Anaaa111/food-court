package com.food.service;

import com.food.dto.UserLoginDTO;
import com.food.entity.User;

public interface UserService {

    User login(UserLoginDTO userLoginDTO);
}
