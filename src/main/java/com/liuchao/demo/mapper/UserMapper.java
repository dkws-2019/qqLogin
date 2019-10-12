package com.liuchao.demo.mapper;

import com.liuchao.demo.entity.User;

public interface UserMapper {

    public User findByNamePassword(User user);

    public User findByOpenId(String openId);
}
