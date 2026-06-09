package org.petcare.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具
 * 用于生成BCrypt加密的密码
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成admin123的哈希
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        
        System.out.println("========================================");
        System.out.println("管理员账号密码信息");
        System.out.println("========================================");
        System.out.println("用户名: admin");
        System.out.println("密码: " + adminPassword);
        System.out.println("BCrypt哈希: " + adminHash);
        System.out.println("========================================");
        System.out.println();
        
        // 生成SQL语句
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE `user` SET `password` = '" + adminHash + "' WHERE `username` = 'admin';");
        System.out.println();
        
        // 生成INSERT语句
        System.out.println("SQL插入语句:");
        System.out.println("INSERT INTO `user` (`username`, `password`, `phone`, `real_name`, `role`, `status`) VALUES ('admin', '" + adminHash + "', '13800138000', '系统管理员', 'ADMIN', 1);");
    }
}
