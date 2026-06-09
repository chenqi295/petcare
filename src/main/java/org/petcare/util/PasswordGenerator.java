package org.petcare.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具
 * 运行这个类的 main 方法来生成正确的 BCrypt 密码哈希
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的哈希
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("管理员账号密码信息");
        System.out.println("========================================");
        System.out.println("用户名: admin");
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println("========================================");
        System.out.println();
        System.out.println("请复制上面的哈希值，然后执行以下SQL：");
        System.out.println();
        System.out.println("UPDATE `user` SET `password` = '" + hash + "' WHERE `username` = 'admin';");
        System.out.println();
        System.out.println("========================================");
    }
}
