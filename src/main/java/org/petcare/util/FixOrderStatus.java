package org.petcare.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 修复订单状态工具类
 * 用于修复技师完成服务后订单状态未更新的问题
 */
public class FixOrderStatus {
    
    // 数据库配置（请根据实际情况修改）
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petcare?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234"; // 请修改为您的数据库密码
    
    public static void main(String[] args) {
        System.out.println("========== 开始修复订单状态 ==========");
        
        String sql = "UPDATE orders o " +
                     "INNER JOIN appointments a ON o.appointment_id = a.id " +
                     "SET o.status = 3, o.update_time = NOW() " +
                     "WHERE o.status = 2 " +
                     "AND o.pay_status = 1 " +
                     "AND a.status = 4 " +
                     "AND o.deleted = 0 " +
                     "AND o.order_no = 'ORDE5076CEC08A1'";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // 执行更新
            int rows = stmt.executeUpdate(sql);
            
            if (rows > 0) {
                System.out.println("✓ 成功修复 " + rows + " 个订单");
                System.out.println("✓ 订单 ORDE5076CEC08A1 状态已更新为已完成");
            } else {
                System.out.println("✗ 没有找到需要修复的订单");
                System.out.println("  可能原因：");
                System.out.println("  1. 订单号不正确");
                System.out.println("  2. 订单状态已经是已完成");
                System.out.println("  3. 关联的预约状态不是已完成");
            }
            
        } catch (Exception e) {
            System.err.println("✗ 修复失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========== 修复完成 ==========");
    }
}
