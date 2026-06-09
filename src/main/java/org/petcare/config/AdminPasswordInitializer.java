package org.petcare.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.User;
import org.petcare.mapper.UserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动检查并更新管理员密码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========================================");
        log.info("检查管理员账号密码...");
        log.info("========================================");

        // 查找 admin 用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User adminUser = userMapper.selectOne(wrapper);

        if (adminUser != null) {
            // 使用正确的密码 admin123
            String password = "admin123";
            String newHash = passwordEncoder.encode(password);
            
            log.info("找到管理员账号: admin");
            log.info("当前密码哈希: {}", adminUser.getPassword());
            log.info("生成新的密码哈希: {}", newHash);
            
            // 更新密码
            adminUser.setPassword(newHash);
            userMapper.updateById(adminUser);
            
            log.info("✓ 管理员密码已更新！");
            log.info("用户名: admin");
            log.info("密码: admin123");
            log.info("========================================");
        } else {
            log.warn("未找到管理员账号 admin");
            log.info("========================================");
        }
    }
}
