package org.petcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.petcare.common.Result;
import org.petcare.entity.User;
import org.petcare.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public TestController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    @GetMapping("/hello")
    public Result<String> hello() {
        String message = "Hello, PetCare Platform!";
        return Result.success(message);
    }
    
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "PetCare Platform");
        info.put("version", "1.0.0");
        info.put("description", "宠物服务预约平台");
        return Result.success(info);
    }
    
    /**
     * 重置admin密码为admin123（仅用于测试）
     */
    @GetMapping("/reset-admin")
    public Result<String> resetAdminPassword() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User admin = userMapper.selectOne(wrapper);
        
        if (admin == null) {
            return Result.successMessage("admin用户不存在");
        }
        
        // 重置密码为admin123
        String newPassword = passwordEncoder.encode("admin123");
        admin.setPassword(newPassword);
        userMapper.updateById(admin);
        
        return Result.success("admin密码已重置为: admin123");
    }
}
