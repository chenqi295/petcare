package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.Appointment;
import org.petcare.entity.Order;
import org.petcare.entity.Pet;
import org.petcare.entity.Review;
import org.petcare.entity.ServiceItem;
import org.petcare.entity.Technician;
import org.petcare.entity.User;
import org.petcare.mapper.AppointmentMapper;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.PetMapper;
import org.petcare.mapper.ServiceItemMapper;
import org.petcare.mapper.UserMapper;
import org.petcare.service.ReviewService;
import org.petcare.service.TechnicianService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技师控制器
 */
@RestController
@RequestMapping("/technician")
@RequiredArgsConstructor
public class TechnicianController {
    
    private final TechnicianService technicianService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final PetMapper petMapper;
    private final AppointmentMapper appointmentMapper;
    private final OrderMapper orderMapper;
    private final ReviewService reviewService;
    
    /**
     * 获取当前技师信息
     */
    @GetMapping("/info")
    public Result<Technician> getMyInfo(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        return Result.success(technician);
    }
    
    /**
     * 更新技师信息
     */
    @PutMapping("/update")
    public Result<Technician> updateInfo(@RequestHeader("Authorization") String token,
                                         @RequestBody Technician technician) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        // 获取技师ID
        Technician existing = technicianService.getByUserId(userId);
        if (existing == null) {
            return Result.error("技师信息不存在");
        }
        
        technician.setId(existing.getId());
        Technician updated = technicianService.updateInfo(technician);
        
        return Result.success("更新成功", updated);
    }
    
    /**
     * 更新工作状态
     */
    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, Integer> request) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        Integer status = request.get("status");
        technicianService.updateStatus(technician.getId(), status);
        
        return Result.successMessage("状态更新成功");
    }
    
    /**
     * 获取工作台统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        Map<String, Object> stats = technicianService.getDashboardStats(technician.getId());
        
        return Result.success(stats);
    }
    
    /**
     * 获取订单列表
     */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> getOrders(@RequestHeader("Authorization") String token,
                                         @RequestParam(required = false) Integer status) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        List<Order> orders = technicianService.getOrders(technician.getId(), status);
        
        // 为每个订单添加服务名称和预约状态
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
            
            // 获取预约状态
            if (order.getAppointmentId() != null) {
                Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
                map.put("appointmentStatus", appointment != null ? appointment.getStatus() : null);
            } else {
                map.put("appointmentStatus", null);
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取预约列表
     */
    @GetMapping("/appointments")
    public Result<List<Map<String, Object>>> getAppointments(@RequestHeader("Authorization") String token,
                                                     @RequestParam(required = false) String status) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        // 解析状态参数，支持多个状态（逗号分隔，如 "2,3,4"）
        List<Integer> statusList = null;
        if (status != null && !status.isEmpty()) {
            statusList = Arrays.stream(status.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        
        List<Appointment> appointments = technicianService.getAppointments(technician.getId(), statusList);
        
        // 为每个预约添加宠物品种和服务名称
        List<Map<String, Object>> result = appointments.stream().map(appointment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", appointment.getId());
            map.put("appointmentNo", appointment.getAppointmentNo());
            map.put("userId", appointment.getUserId());
            map.put("petId", appointment.getPetId());
            map.put("serviceId", appointment.getServiceId());
            map.put("technicianId", appointment.getTechnicianId());
            map.put("appointmentTime", appointment.getAppointmentTime());
            map.put("remark", appointment.getRemark());
            map.put("status", appointment.getStatus());
            map.put("cancelReason", appointment.getCancelReason());
            map.put("createTime", appointment.getCreateTime());
            map.put("updateTime", appointment.getUpdateTime());
            
            // 获取宠物品种
            if (appointment.getPetId() != null) {
                Pet pet = petMapper.selectById(appointment.getPetId());
                map.put("petBreed", pet != null ? pet.getBreed() : "-");
            } else {
                map.put("petBreed", "-");
            }
            
            // 获取服务名称
            if (appointment.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(appointment.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取服务历史记录
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        List<Order> history = technicianService.getHistory(technician.getId());
        
        // 为每个订单添加服务名称
        List<Map<String, Object>> result = history.stream().map(order -> {
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
            // 添加完成时间（对于已完成的订单，使用updateTime）
            if (order.getStatus() != null && order.getStatus() == 3) {
                map.put("completeTime", order.getUpdateTime());
            } else {
                map.put("completeTime", null);
            }
            
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
     * 获取所有工作中的技师列表（公开接口）
     */
    @GetMapping("/list")
    public Result<List<Technician>> getTechnicianList() {
        List<Technician> technicians = technicianService.getWorkingTechnicians();
        return Result.success(technicians);
    }
    
    /**
     * 获取所有技师列表（公开接口，包含详细信息）
     */
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllTechniciansPublic() {
        List<Technician> technicians = technicianService.getAllTechnicians();
        
        // 为每个技师添加用户名信息
        List<Map<String, Object>> result = technicians.stream().map(technician -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", technician.getId());
            map.put("userId", technician.getUserId());
            map.put("name", technician.getName());
            map.put("phone", technician.getPhone());
            map.put("skills", technician.getSkills());
            map.put("workYears", technician.getWorkYears());
            map.put("rating", technician.getRating() != null && technician.getRating() > 0 ? technician.getRating() : 5.0);
            map.put("serviceCount", technician.getServiceCount());
            map.put("status", technician.getStatus());
            map.put("introduction", technician.getIntroduction());
            map.put("avatar", technician.getAvatar());
            map.put("createTime", technician.getCreateTime());
            
            // 获取用户名
            User user = userMapper.selectById(technician.getUserId());
            map.put("username", user != null ? user.getUsername() : "-");
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取所有技师列表（管理端）
     */
    @GetMapping("/admin/list")
    public Result<List<Map<String, Object>>> getAllTechnicians() {
        List<Technician> technicians = technicianService.getAllTechnicians();
        
        // 为每个技师添加用户状态信息
        List<Map<String, Object>> result = technicians.stream().map(technician -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", technician.getId());
            map.put("userId", technician.getUserId());
            map.put("name", technician.getName());
            map.put("phone", technician.getPhone());
            map.put("skills", technician.getSkills());
            map.put("workYears", technician.getWorkYears());
            map.put("rating", technician.getRating() != null && technician.getRating() > 0 ? technician.getRating() : 5.0);
            map.put("serviceCount", technician.getServiceCount());
            map.put("status", technician.getStatus());
            map.put("introduction", technician.getIntroduction());
            map.put("createTime", technician.getCreateTime());
            
            // 获取用户状态和用户名
            User user = userMapper.selectById(technician.getUserId());
            map.put("userStatus", user != null ? user.getStatus() : 0);
            map.put("username", user != null ? user.getUsername() : "-");
            map.put("userRole", user != null ? user.getRole() : "-");
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 更新技师账号状态（启用/禁用）- 管理端
     */
    @PutMapping("/admin/{id}/status")
    public Result<Void> updateTechnicianAccountStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Technician technician = technicianService.getById(id);
        if (technician == null) {
            return Result.error("技师不存在");
        }
        
        Integer status = request.get("status");
        User user = userMapper.selectById(technician.getUserId());
        if (user != null) {
            user.setStatus(status);
            userMapper.updateById(user);
        }
        
        return Result.successMessage("状态更新成功");
    }
    
    /**
     * 获取技师的评价列表
     */
    @GetMapping("/reviews")
    public Result<List<Map<String, Object>>> getMyReviews(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        
        Technician technician = technicianService.getByUserId(userId);
        if (technician == null) {
            return Result.error("技师信息不存在");
        }
        
        // 获取该技师的所有评价
        List<Review> reviews = reviewService.getTechnicianReviews(technician.getId());
        
        // 为每个评价添加订单号和服务名称
        List<Map<String, Object>> result = reviews.stream().map(review -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", review.getId());
            map.put("userId", review.getUserId());
            map.put("orderId", review.getOrderId());
            map.put("serviceId", review.getServiceId());
            map.put("technicianId", review.getTechnicianId());
            map.put("rating", review.getRating());
            map.put("content", review.getContent());
            map.put("images", review.getImages());
            map.put("status", review.getStatus());
            map.put("createTime", review.getCreateTime());
            map.put("updateTime", review.getUpdateTime());
            
            // 获取订单号
            if (review.getOrderId() != null) {
                Order order = orderMapper.selectById(review.getOrderId());
                map.put("orderNo", order != null ? order.getOrderNo() : "-");
            } else {
                map.put("orderNo", "-");
            }
            
            // 获取服务名称
            if (review.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(review.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            // 获取评价用户信息
            if (review.getUserId() != null) {
                User user = userMapper.selectById(review.getUserId());
                map.put("userName", user != null ? user.getUsername() : "匿名用户");
            } else {
                map.put("userName", "匿名用户");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    /**
     * 获取指定技师的评价列表（公开接口）
     */
    @GetMapping("/{technicianId}/reviews/public")
    public Result<List<Map<String, Object>>> getTechnicianReviewsPublic(@PathVariable Long technicianId) {
        // 获取该技师的所有评价
        List<Review> reviews = reviewService.getTechnicianReviews(technicianId);
        
        // 为每个评价添加订单号和服务名称
        List<Map<String, Object>> result = reviews.stream().map(review -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", review.getId());
            map.put("userId", review.getUserId());
            map.put("orderId", review.getOrderId());
            map.put("serviceId", review.getServiceId());
            map.put("technicianId", review.getTechnicianId());
            map.put("rating", review.getRating());
            map.put("content", review.getContent());
            map.put("images", review.getImages());
            map.put("status", review.getStatus());
            map.put("createTime", review.getCreateTime());
            map.put("updateTime", review.getUpdateTime());
            
            // 获取订单号
            if (review.getOrderId() != null) {
                Order order = orderMapper.selectById(review.getOrderId());
                map.put("orderNo", order != null ? order.getOrderNo() : "-");
            } else {
                map.put("orderNo", "-");
            }
            
            // 获取服务名称
            if (review.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(review.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            // 获取评价用户信息
            if (review.getUserId() != null) {
                User user = userMapper.selectById(review.getUserId());
                map.put("userName", user != null ? user.getUsername() : "匿名用户");
            } else {
                map.put("userName", "匿名用户");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
}
