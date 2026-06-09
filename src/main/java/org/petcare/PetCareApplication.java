package org.petcare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 宠物服务预约平台 - 主启动类
 */
@SpringBootApplication
@MapperScan("org.petcare.mapper")
@EnableScheduling // 启用定时任务
public class PetCareApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PetCareApplication.class, args);
        System.out.println("========================================");
        System.out.println("   宠物服务预约平台启动成功！");
        System.out.println("   API地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}

