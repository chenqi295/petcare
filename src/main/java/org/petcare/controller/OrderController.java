package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.Order;
import org.petcare.entity.ServiceItem;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.ServiceItemMapper;
import org.petcare.service.OrderService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final ServiceItemMapper serviceItemMapper;
    private final OrderMapper orderMapper;
    
    /**
     * 获取我的订单列表
     */
    @GetMapping("/my-orders")
    public Result<List<Map<String, Object>>> getMyOrders(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        List<Order> orders = orderService.getUserOrders(userId);
        
        // 为每个订单添加服务名称
        List<Map<String, Object>> result = orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("appointmentId", order.getAppointmentId());
            map.put("serviceId", order.getServiceId());
            map.put("technicianId", order.getTechnicianId());
            map.put("petId", order.getPetId());
            map.put("amount", order.getAmount());
            map.put("payStatus", order.getPayStatus());
            map.put("payMethod", order.getPayMethod());
            map.put("payTime", order.getPayTime());
            map.put("status", order.getStatus());
            map.put("remark", order.getRemark());
            map.put("createTime", order.getCreateTime());
            map.put("updateTime", order.getUpdateTime());
            
            // 获取服务名称
            if (order.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(order.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(order);
    }
    
    /**
     * 支付订单（模拟）
     */
    @PostMapping("/pay/{id}")
    public Result<Void> payOrder(@PathVariable Long id,
                                 @RequestParam(defaultValue = "ALIPAY") String payMethod) {
        orderService.payOrder(id, payMethod);
        return Result.successMessage("支付成功");
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        orderService.cancelOrder(id, userId);
        return Result.successMessage("取消成功");
    }
    
    /**
     * 获取所有订单列表（管理端）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        
        // 为每个订单添加服务名称
        List<Map<String, Object>> result = orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("appointmentId", order.getAppointmentId());
            map.put("serviceId", order.getServiceId());
            map.put("technicianId", order.getTechnicianId());
            map.put("petId", order.getPetId());
            map.put("amount", order.getAmount());
            map.put("payStatus", order.getPayStatus());
            map.put("payMethod", order.getPayMethod());
            map.put("payTime", order.getPayTime());
            map.put("status", order.getStatus());
            map.put("remark", order.getRemark());
            map.put("createTime", order.getCreateTime());
            map.put("updateTime", order.getUpdateTime());
            
            // 获取服务名称
            if (order.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(order.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取收入统计（管理端）
     */
    @GetMapping("/revenue-stats")
    public Result<Map<String, Object>> getRevenueStats() {
        LocalDate now = LocalDate.now();
        LocalDateTime yearStart = now.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime nowTime = LocalDateTime.now();
        
        // 获取所有已支付订单（包括已支付和已完成状态）
        List<Order> paidOrders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getPayStatus, 1)
                .ge(Order::getStatus, 2)
                .orderByDesc(Order::getPayTime)
        );
        
        // 计算年收入
        BigDecimal yearlyRevenue = paidOrders.stream()
            .filter(order -> order.getPayTime() != null && order.getPayTime().isAfter(yearStart))
            .map(Order::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算月收入
        BigDecimal monthlyRevenue = paidOrders.stream()
            .filter(order -> order.getPayTime() != null && order.getPayTime().isAfter(monthStart))
            .map(Order::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算总收入
        BigDecimal totalRevenue = paidOrders.stream()
            .map(Order::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("yearlyRevenue", yearlyRevenue);
        stats.put("monthlyRevenue", monthlyRevenue);
        stats.put("year", now.getYear());
        stats.put("month", now.getMonthValue());
        
        return Result.success(stats);
    }
    
    /**
     * 获取近七天的收入统计
     */
    @GetMapping("/seven-day-revenue")
    public Result<List<Map<String, Object>>> getSevenDayRevenue() {
        LocalDate now = LocalDate.now();
        
        // 获取所有已支付订单
        List<Order> paidOrders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getPayStatus, 1)
                .ge(Order::getStatus, 2)
                .orderByDesc(Order::getPayTime)
        );
        
        // 构建近七天的日期列表
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            // 计算当天的收入
            BigDecimal dailyRevenue = paidOrders.stream()
                .filter(order -> order.getPayTime() != null 
                    && !order.getPayTime().isBefore(startOfDay) 
                    && order.getPayTime().isBefore(endOfDay))
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("fullDate", date.toString());
            dayData.put("revenue", dailyRevenue);
            result.add(dayData);
        }
        
        return Result.success(result);
    }
}
