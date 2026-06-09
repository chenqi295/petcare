package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.Appointment;
import org.petcare.entity.Order;
import org.petcare.entity.Technician;
import org.petcare.mapper.AppointmentMapper;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.TechnicianMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技师服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TechnicianService {
    
    private final TechnicianMapper technicianMapper;
    private final OrderMapper orderMapper;
    private final AppointmentMapper appointmentMapper;
    
    /**
     * 根据用户ID获取技师信息
     */
    public Technician getByUserId(Long userId) {
        LambdaQueryWrapper<Technician> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Technician::getUserId, userId);
        return technicianMapper.selectOne(wrapper);
    }
    
    /**
     * 根据技师ID获取技师信息
     */
    public Technician getById(Long id) {
        return technicianMapper.selectById(id);
    }
    
    /**
     * 更新技师信息
     */
    public Technician updateInfo(Technician technician) {
        Technician existing = technicianMapper.selectById(technician.getId());
        if (existing == null) {
            throw new RuntimeException("技师不存在");
        }
        
        // 更新允许修改的字段
        existing.setName(technician.getName());
        existing.setPhone(technician.getPhone());
        existing.setIntroduction(technician.getIntroduction());
        existing.setSkills(technician.getSkills());
        existing.setWorkYears(technician.getWorkYears());
        existing.setAvatar(technician.getAvatar());
        
        technicianMapper.updateById(existing);
        return existing;
    }
    
    /**
     * 更新工作状态
     */
    public void updateStatus(Long technicianId, Integer status) {
        Technician technician = technicianMapper.selectById(technicianId);
        if (technician != null) {
            technician.setStatus(status);
            technicianMapper.updateById(technician);
        }
    }
    
    /**
     * 获取技师工作台统计数据
     */
    public Map<String, Object> getDashboardStats(Long technicianId) {
        Map<String, Object> stats = new HashMap<>();
        Technician technician = technicianMapper.selectById(technicianId);
        
        log.info("查询技师工作台数据 - 技师ID: {}", technicianId);
        
        if (technician != null) {
            // 统计待处理预约（待确认状态的预约）
            LambdaQueryWrapper<Appointment> pendingWrapper = new LambdaQueryWrapper<>();
            pendingWrapper.eq(Appointment::getTechnicianId, technicianId)
                    .eq(Appointment::getStatus, 1); // 待确认状态
            long pendingOrders = appointmentMapper.selectCount(pendingWrapper);
            log.info("待处理预约数量: {}", pendingOrders);
            
            // 统计今日预约（今天的所有预约，排除已取消的）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = now.toLocalDate().plusDays(1).atStartOfDay();
            
            LambdaQueryWrapper<Appointment> todayAppointmentWrapper = new LambdaQueryWrapper<>();
            todayAppointmentWrapper.eq(Appointment::getTechnicianId, technicianId)
                    .ge(Appointment::getAppointmentTime, startOfDay)
                    .lt(Appointment::getAppointmentTime, endOfDay)
                    .ne(Appointment::getStatus, 0); // 排除已取消状态（0）
            long todayAppointments = appointmentMapper.selectCount(todayAppointmentWrapper);
            log.info("今日预约数量: {}", todayAppointments);
            
            // 统计已完成服务（已完成的订单数量）
            LambdaQueryWrapper<Order> completedOrderWrapper = new LambdaQueryWrapper<>();
            completedOrderWrapper.eq(Order::getTechnicianId, technicianId)
                    .eq(Order::getStatus, 3); // 已完成状态
            long completedCount = orderMapper.selectCount(completedOrderWrapper);
            log.info("已完成服务数量: {}", completedCount);
            
            stats.put("pendingOrders", pendingOrders);
            stats.put("todayAppointments", todayAppointments);
            stats.put("completedCount", completedCount);
            stats.put("rating", technician.getRating() != null && technician.getRating() > 0 ? technician.getRating() : 5.0);
        } else {
            log.warn("技师不存在 - 技师ID: {}", technicianId);
            stats.put("pendingOrders", 0);
            stats.put("todayAppointments", 0);
            stats.put("completedCount", 0);
            stats.put("rating", 5.0);
        }
        
        return stats;
    }
    
    /**
     * 获取技师的订单列表
     */
    public List<Order> getOrders(Long technicianId, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTechnicianId, technicianId);
        
        // 如果指定了状态，按状态筛选
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
    
    /**
     * 获取技师的预约列表
     */
    public List<Appointment> getAppointments(Long technicianId, List<Integer> statusList) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getTechnicianId, technicianId);
        
        log.info("查询技师预约列表 - 技师ID: {}, 状态筛选: {}", technicianId, statusList);
        
        // 如果指定了状态列表，按状态筛选
        if (statusList != null && !statusList.isEmpty()) {
            wrapper.in(Appointment::getStatus, statusList);
            log.info("按状态筛选: {}", statusList);
        }
        
        wrapper.orderByDesc(Appointment::getAppointmentTime);
        List<Appointment> result = appointmentMapper.selectList(wrapper);
        log.info("查询到预约数量: {}", result.size());
        return result;
    }
    
    /**
     * 获取技师的服务历史记录（已完成的订单）
     */
    public List<Order> getHistory(Long technicianId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTechnicianId, technicianId)
                .eq(Order::getStatus, 3) // 已完成状态
                .orderByDesc(Order::getUpdateTime);
        return orderMapper.selectList(wrapper);
    }
    
    /**
     * 获取所有工作中的技师列表
     */
    public List<Technician> getWorkingTechnicians() {
        LambdaQueryWrapper<Technician> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Technician::getStatus, 1) // 工作中
                .orderByDesc(Technician::getRating)
                .orderByDesc(Technician::getServiceCount);
        return technicianMapper.selectList(wrapper);
    }
    
    /**
     * 获取所有技师列表（管理端）
     */
    public List<Technician> getAllTechnicians() {
        LambdaQueryWrapper<Technician> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Technician::getCreateTime);
        return technicianMapper.selectList(wrapper);
    }
}
