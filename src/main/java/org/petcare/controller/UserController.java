package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.User;
import org.petcare.service.UserService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = userService.register(
            request.getUsername(), 
            request.getPassword(), 
            request.getPhone(),
            request.getRealName(),
            request.getRole()
        );
        return Result.success("注册成功", user);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        // 从token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserInfo(userId);
        data.put("userInfo", user);
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUserInfo(@RequestHeader("Authorization") String token,
                                       @RequestBody User user) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        user.setId(userId);
        User updatedUser = userService.updateUserInfo(user);
        return Result.success("更新成功", updatedUser);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestHeader("Authorization") String token,
                                       @RequestBody ChangePasswordRequest request) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.successMessage("密码修改成功");
    }
    
    /**
     * 通过手机号重置密码（忘记密码）
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPasswordByPhone(request.getPhone(), request.getNewPassword());
        return Result.successMessage("密码重置成功");
    }
    
    /**
     * 获取所有用户列表（管理端）
     */
    @GetMapping("/list")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }
    
    /**
     * 获取非管理员用户数量（用于仪表台统计）
     */
    @GetMapping("/count/non-admin")
    public Result<Long> getNonAdminUserCount() {
        long count = userService.getNonAdminUserCount();
        return Result.success(count);
    }
    
    /**
     * 更新用户信息（管理端）
     */
    @PutMapping("/update/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return Result.success("更新成功", updatedUser);
    }
    
    // 请求DTO类
    static class RegisterRequest {
        private String username;
        private String password;
        private String phone;
        private String realName;
        private String role;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    static class ResetPasswordRequest {
        private String phone;
        private String newPassword;
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
