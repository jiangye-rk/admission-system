package com.admission.service.impl;

import com.admission.entity.ScoreSegment;
import com.admission.entity.User;
import com.admission.mapper.ScoreSegmentMapper;
import com.admission.mapper.UserMapper;
import com.admission.service.IUserService;
import com.admission.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ScoreSegmentMapper scoreSegmentMapper;
    
    // 天津市各区行政区划代码（第3-8位）
    private static final Set<String> TIANJIN_DISTRICT_CODES = new HashSet<>(Arrays.asList(
        "120101", // 和平区
        "120102", // 河东区
        "120103", // 河西区
        "120104", // 南开区
        "120105", // 河北区
        "120106", // 红桥区
        "120107", // 滨海新区（塘沽）
        "120108", // 滨海新区（汉沽）
        "120109", // 滨海新区（大港）
        "120110", // 东丽区
        "120111", // 西青区
        "120112", // 津南区
        "120113", // 北辰区
        "120221", // 宁河区
        "120222", // 武清区
        "120223", // 静海区
        "120224", // 宝坻区
        "120225"  // 蓟州区
    ));

    @Override
    public User login(String username, String password) {
        // 使用 MyBatis-Plus 内置方法查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptPwd = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!encryptPwd.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        user.setLastLoginTime(new Date());
        this.updateById(user);
        return user;
    }

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        User exist = lambdaQuery().eq(User::getUsername, user.getUsername()).one();
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查考生号是否已存在
        User existKsh = lambdaQuery().eq(User::getKsh, user.getKsh()).one();
        if (existKsh != null) {
            throw new RuntimeException("该考生号已被注册");
        }
        // 字段校验
        if (user.getKsh() == null || user.getKsh().length() != 14) {
            throw new RuntimeException("考生号必须为14位");
        }
        // 验证考生号格式
        validateKsh(user.getKsh());
        
        if (user.getSjh() == null || !user.getSjh().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (user.getXk() == null || user.getXk().split(",").length != 3) {
            throw new RuntimeException("请选择3门选考科目");
        }
        // 根据分数查询位次
        if (user.getFs() != null && user.getWc() == null) {
            Integer wc = getRankByScore(user.getFs());
            if (wc != null) {
                user.setWc(wc);
            }
        }
        // 密码加密
        String encryptPwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptPwd);
        user.setRole("guest");
        user.setStatus(1);
        user.setCreateTime(new Date());
        this.save(user);
        return user;
    }
    
    /**
     * 验证考生号格式
     * 第1-2位：年份后两位
     * 第3-8位：区行政区划代码
     * 第9-14位：顺序号
     */
    private void validateKsh(String ksh) {
        // 验证是否为14位数字
        if (!ksh.matches("^\\d{14}$")) {
            throw new RuntimeException("考生号必须为14位数字");
        }
        
        // 提取年份（第1-2位）
        String year = ksh.substring(0, 2);
        // 验证年份是否合理（20-30年之间）
        int yearInt = Integer.parseInt(year);
        if (yearInt < 20 || yearInt > 30) {
            throw new RuntimeException("考生号年份格式不正确");
        }
        
        // 提取区行政区划代码（第3-8位）
        String districtCode = ksh.substring(2, 8);
        if (!TIANJIN_DISTRICT_CODES.contains(districtCode)) {
            throw new RuntimeException("考生号行政区划代码不正确，请输入正确的天津市区县代码");
        }
        
        // 提取顺序号（第9-14位）
        String seqNo = ksh.substring(8, 14);
        // 验证顺序号是否为6位数字
        if (!seqNo.matches("^\\d{6}$")) {
            throw new RuntimeException("考生号顺序号格式不正确");
        }
    }

    @Override
    public String generateToken(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        redisTemplate.opsForValue().set("token:" + user.getId(), token, 24, TimeUnit.HOURS);
        return token;
    }

    @Override
    public User getUserByToken(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) return null;
        return this.getById(userId);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptPwd = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptPwd);
        this.updateById(user);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public Integer getRankByScore(Integer score) {
        LambdaQueryWrapper<ScoreSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScoreSegment::getFs, score);
        wrapper.orderByDesc(ScoreSegment::getNf);
        wrapper.last("LIMIT 1");
        ScoreSegment segment = scoreSegmentMapper.selectOne(wrapper);
        if (segment != null) {
            return segment.getLjrs();
        }
        // 如果没有精确匹配，找最接近的分数
        wrapper = new LambdaQueryWrapper<>();
        wrapper.le(ScoreSegment::getFs, score);
        wrapper.orderByDesc(ScoreSegment::getFs);
        wrapper.orderByDesc(ScoreSegment::getNf);
        wrapper.last("LIMIT 1");
        segment = scoreSegmentMapper.selectOne(wrapper);
        return segment != null ? segment.getLjrs() : null;
    }

    @Override
    public User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }
}
