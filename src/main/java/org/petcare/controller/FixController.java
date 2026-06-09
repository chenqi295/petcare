package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.common.Result;
import org.petcare.entity.Appointment;
import org.petcare.entity.Order;
import org.petcare.entity.Review;
import org.petcare.entity.Technician;
import org.petcare.mapper.AppointmentMapper;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.ReviewMapper;
import org.petcare.mapper.TechnicianMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 修复控制器
 * 用于修复历史数据问题
 */
@Slf4j
@RestController
@RequestMapping("/fix")
@RequiredArgsConstructor
public class FixController {
    
    private final OrderMapper orderMapper;
    private final AppointmentMapper appointmentMapper;
    private final ReviewMapper reviewMapper;
    private final TechnicianMapper technicianMapper;
    
    /**
     * 修复订单状态：将所有预约已完成但订单状态不是已完成的订单更新为已完成
     */
    @PostMapping("/order-status")
    public Result<String> fixOrderStatus() {
        try {
            log.info("========== 开始修复订单状态 ==========");
            
            // 查询所有订单
            java.util.List<Order> allOrders = orderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                    .eq(Order::getStatus, 2)  // 已支付
                    .eq(Order::getPayStatus, 1)  // 已支付
                    .eq(Order::getDeleted, 0)
            );
            
            int fixedCount = 0;
            
            for (Order order : allOrders) {
                if (order.getAppointmentId() != null) {
                    Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
                    
                    if (appointment != null && appointment.getStatus() == 4) {
                        // 预约已完成，但订单状态不是已完成
                        order.setStatus(3); // 已完成
                        orderMapper.updateById(order);
                        fixedCount++;
                        
                        log.info("修复订单: {} - 订单号: {}, 预约ID: {}", 
                                order.getId(), order.getOrderNo(), order.getAppointmentId());
                    }
                }
            }
            
            log.info("========== 订单状态修复完成，共修复 {} 个订单 ==========", fixedCount);
            
            return Result.success("成功修复 " + fixedCount + " 个订单状态");
            
        } catch (Exception e) {
            log.error("修复订单状态失败", e);
            return Result.error("修复失败: " + e.getMessage());
        }
    }
    
    /**
     * 查看需要修复的订单
     */
    @GetMapping("/order-status/check")
    public Result<java.util.List<java.util.Map<String, Object>>> checkOrderStatus() {
        try {
            java.util.List<Order> allOrders = orderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                    .eq(Order::getStatus, 2)
                    .eq(Order::getPayStatus, 1)
                    .eq(Order::getDeleted, 0)
            );
            
            java.util.List<java.util.Map<String, Object>> needFix = new java.util.ArrayList<>();
            
            for (Order order : allOrders) {
                if (order.getAppointmentId() != null) {
                    Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
                    
                    if (appointment != null && appointment.getStatus() == 4) {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("orderId", order.getId());
                        map.put("orderNo", order.getOrderNo());
                        map.put("orderStatus", order.getStatus());
                        map.put("payStatus", order.getPayStatus());
                        map.put("appointmentId", order.getAppointmentId());
                        map.put("appointmentStatus", appointment.getStatus());
                        needFix.add(map);
                    }
                }
            }
            
            return Result.success(needFix);
            
        } catch (Exception e) {
            log.error("检查订单状态失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 修复评价表中的technician_id字段
     */
    @PostMapping("/review-technician")
    public Result<String> fixReviewTechnicianId() {
        try {
            log.info("========== 开始修复评价表中的technician_id ==========");
            
            // 查询所有technician_id为空的评价
            List<Review> reviews = reviewMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Review>()
                    .isNull(Review::getTechnicianId)
            );
            
            int fixedCount = 0;
            
            for (Review review : reviews) {
                if (review.getOrderId() != null) {
                    Order order = orderMapper.selectById(review.getOrderId());
                    
                    if (order != null && order.getTechnicianId() != null) {
                        // 从订单中获取technician_id
                        review.setTechnicianId(order.getTechnicianId());
                        reviewMapper.updateById(review);
                        fixedCount++;
                        
                        log.info("修复评价: reviewId={}, orderId={}, technicianId={}", 
                                review.getId(), review.getOrderId(), order.getTechnicianId());
                    }
                }
            }
            
            log.info("========== 评价technician_id修复完成，共修复 {} 个评价 ==========", fixedCount);
            
            return Result.success("成功修复 " + fixedCount + " 个评价的technician_id");
            
        } catch (Exception e) {
            log.error("修复评价technician_id失败", e);
            return Result.error("修复失败: " + e.getMessage());
        }
    }
    
    /**
     * 重新计算所有技师的评分
     */
    @PostMapping("/technician-rating")
    public Result<String> recalculateTechnicianRatings() {
        try {
            log.info("========== 开始重新计算技师评分 ==========");
            
            // 查询所有技师
            List<Technician> technicians = technicianMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Technician>()
            );
            
            int updatedCount = 0;
            
            for (Technician technician : technicians) {
                // 查询该技师的所有评价
                List<Review> reviews = reviewMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Review>()
                        .eq(Review::getTechnicianId, technician.getId())
                        .eq(Review::getStatus, 1)
                );
                
                if (!reviews.isEmpty()) {
                    // 计算平均评分
                    double averageRating = reviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(5.0);
                    
                    // 四舍五入保留一位小数
                    averageRating = Math.round(averageRating * 10.0) / 10.0;
                    
                    // 更新技师信息
                    technician.setRating(averageRating);
                    technician.setServiceCount(reviews.size());
                    technicianMapper.updateById(technician);
                    updatedCount++;
                    
                    log.info("更新技师评分: technicianId={}, rating={}, serviceCount={}", 
                            technician.getId(), averageRating, reviews.size());
                }
            }
            
            log.info("========== 技师评分重新计算完成，共更新 {} 个技师 ==========", updatedCount);
            
            return Result.success("成功重新计算 " + updatedCount + " 个技师的评分");
            
        } catch (Exception e) {
            log.error("重新计算技师评分失败", e);
            return Result.error("计算失败: " + e.getMessage());
        }
    }
}
