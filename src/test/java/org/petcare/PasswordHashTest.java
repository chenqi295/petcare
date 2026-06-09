package org.petcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成测试
 */
@SpringBootTest
public class PasswordHashTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void generateAdminPasswordHash() {
        String password = "admin123";
        String hash = passwordEncoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("管理员账号密码信息");
        System.out.println("========================================");
        System.out.println("用户名: admin");
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println("========================================");
        System.out.println();
        
        // 生成SQL语句
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE `user` SET `password` = '" + hash + "' WHERE `username` = 'admin';");
        System.out.println();
        
        // 生成INSERT语句
        System.out.println("SQL插入语句:");
        System.out.println("INSERT INTO `user` (`username`, `password`, `phone`, `real_name`, `role`, `status`) VALUES ('admin', '" + hash + "', '13800138000', '系统管理员', 'ADMIN', 1);");
    }
}
