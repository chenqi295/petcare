package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.User;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.TechnicianMapper;
import org.petcare.mapper.UserMapper;
import org.petcare.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final TechnicianMapper technicianMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 验证密码格式
     * 要求：必须包含字母和数字，长度6-20位
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            throw new BusinessException("密码长度必须在6-20位之间");
        }
        
        // 检查是否包含字母
        boolean hasLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
                break;
            }
        }
        
        // 检查是否包含数字
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }
        
        if (!hasLetter) {
            throw new BusinessException("密码必须包含至少一个字母");
        }
        
        if (!hasDigit) {
            throw new BusinessException("密码必须包含至少一个数字");
        }
    }
    
    /**
     * 用户注册
     */
    public User register(String username, String password, String phone, String realName, String role) {
        // 验证密码格式
        validatePassword(password);
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (StringUtils.hasText(phone)) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }
        
        // 验证角色，只允许注册为USER或TECHNICIAN
        if (!"USER".equals(role) && !"TECHNICIAN".equals(role)) {
            role = "USER"; // 默认普通用户
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus(1);
        
        userMapper.insert(user);
        
        // 如果是技师，创建技师信息
        if ("TECHNICIAN".equals(role)) {
            org.petcare.entity.Technician technician = new org.petcare.entity.Technician();
            technician.setUserId(user.getId());
            technician.setName(realName != null ? realName : username);
            technician.setPhone(phone);
            technician.setStatus(1); // 默认工作状态
            technician.setRating(5.0); // 默认评分5分
            technician.setServiceCount(0);
            technicianMapper.insert(technician);
        }
        
        user.setPassword(null); // 不返回密码
        
        return user;
    }
    
    /**
     * 用户登录
     */
    public String login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }
    
    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 更新用户信息
     */
    public User updateUserInfo(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 只允许更新部分字段
        existingUser.setRealName(user.getRealName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAvatar(user.getAvatar());
        
        userMapper.updateById(existingUser);
        existingUser.setPassword(null);
        
        return existingUser;
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 验证新密码格式
        validatePassword(newPassword);
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
    
    /**
     * 获取所有用户列表（管理端）
     */
    public List<User> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        List<User> users = userMapper.selectList(wrapper);
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 更新用户信息（管理端）
     */
    public User updateUser(Long userId, User user) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 只允许更新角色、状态、邮箱、真实姓名
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        
        userMapper.updateById(existingUser);
        existingUser.setPassword(null);
        
        return existingUser;
    }
    
    /**
     * 获取普通用户数量（用于仪表台统计，排除管理员和技师）
     */
    public long getNonAdminUserCount() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "USER");
        return userMapper.selectCount(wrapper);
    }
    
    /**
     * 通过手机号重置密码
     */
    public void resetPasswordByPhone(String phone, String newPassword) {
        // 验证新密码格式
        validatePassword(newPassword);
        
        // 根据手机号查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
