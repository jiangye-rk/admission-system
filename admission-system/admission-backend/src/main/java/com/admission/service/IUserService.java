package com.admission.service;

import com.admission.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {

    User login(String username, String password);

    User register(User user);

    String generateToken(User user);

    User getUserByToken(String token);

    // 重置密码
    void resetPassword(Long userId, String newPassword);

    // 更新用户状态
    void updateStatus(Long userId, Integer status);

    // 根据分数查询位次
    Integer getRankByScore(Integer score);

    // 根据用户名查询用户
    User getByUsername(String username);
}
