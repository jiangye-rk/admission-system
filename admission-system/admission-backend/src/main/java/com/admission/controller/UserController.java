package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.User;
import com.admission.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.login(username, password);
        String token = userService.generateToken(user);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        // 添加考生信息到登录响应
        result.put("ksxm", user.getKsxm());
        result.put("ksh", user.getKsh());
        result.put("fs", user.getFs());
        result.put("wc", user.getWc());
        result.put("xk", user.getXk());
        result.put("gzxx", user.getGzxx());
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 验证必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (user.getKsxm() == null || user.getKsxm().trim().isEmpty()) {
            return Result.error("考生姓名不能为空");
        }
        if (user.getKsh() == null || user.getKsh().trim().isEmpty()) {
            return Result.error("考生号不能为空");
        }
        // 验证考生号格式（14位数字）
        if (!user.getKsh().matches("\\d{14}")) {
            return Result.error("考生号必须是14位数字");
        }
        if (user.getSjh() == null || user.getSjh().trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        // 验证手机号格式
        if (!user.getSjh().matches("1[3-9]\\d{9}")) {
            return Result.error("手机号格式不正确");
        }
        if (user.getFs() == null) {
            return Result.error("分数不能为空");
        }
        if (user.getXk() == null || user.getXk().trim().isEmpty()) {
            return Result.error("选考科目不能为空");
        }
        if (user.getGzxx() == null || user.getGzxx().trim().isEmpty()) {
            return Result.error("高中学校不能为空");
        }

        userService.register(user);
        return Result.success("注册成功");
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestAttribute("username") String username,
                                                    @RequestAttribute("role") String role) {
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("role", role);
        return Result.success(result);
    }

    @GetMapping("/profile")
    public Result<User> getUserProfile(@RequestAttribute("userId") Long userId) {
        System.out.println("=== getUserProfile 被调用，userId = " + userId);
        try {
            User user = userService.getById(userId);
            System.out.println("查询到的 user = " + user);
            if (user == null) {
                System.out.println("user 为 null，将返回错误");
                return Result.error("用户不存在");
            }
            user.setPassword(null); // 脱敏
            return Result.success(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    // 管理员：获取所有用户列表
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        // 脱敏处理
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    // 管理员：重置用户密码
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String newPassword = (String) params.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        userService.resetPassword(userId, newPassword);
        return Result.success("密码重置成功");
    }

    // 管理员：修改用户状态
    @PostMapping("/updateStatus")
    public Result<String> updateUserStatus(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        userService.updateStatus(userId, status);
        return Result.success("状态更新成功");
    }
}
