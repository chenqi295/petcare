package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.Appointment;
import org.petcare.entity.Order;
import org.petcare.entity.ServiceItem;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.AppointmentMapper;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 预约服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentMapper appointmentMapper;
    private final OrderMapper orderMapper;
    private final ServiceItemMapper serviceItemMapper;
    
    /**
     * 创建预约
     */
    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        // 验证服务是否存在
        ServiceItem service = serviceItemMapper.selectById(appointment.getServiceId());
        if (service == null || service.getStatus() == 0) {
            throw new BusinessException("服务不存在或已下架");
        }
        
        // 生成预约编号
        String appointmentNo = "APT" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        appointment.setAppointmentNo(appointmentNo);
        appointment.setStatus(1); // 待确认
        
        // 插入预约记录
        int result = appointmentMapper.insert(appointment);
        if (result <= 0) {
            throw new BusinessException("预约创建失败");
        }
        
        log.info("预约创建成功，ID: {}", appointment.getId());
        
        // 创建订单（使用刚插入的appointment ID）
        createOrder(appointment, service);
        
        return appointment;
    }
    
    /**
     * 创建订单
     */
    private void createOrder(Appointment appointment, ServiceItem service) {
        Order order = new Order();
        order.setOrderNo("ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setUserId(appointment.getUserId());
        order.setAppointmentId(appointment.getId());
        order.setServiceId(appointment.getServiceId());
        order.setTechnicianId(appointment.getTechnicianId());
        order.setPetId(appointment.getPetId());
        order.setAmount(service.getPrice());
        order.setPayStatus(0); // 未支付
        order.setStatus(1); // 待支付
        
        log.info("开始创建订单，预约ID: {}, 用户ID: {}, 宠物ID: {}", 
                appointment.getId(), appointment.getUserId(), appointment.getPetId());
        
        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("订单创建失败");
        }
        
        log.info("订单创建成功，订单ID: {}", order.getId());
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId, String reason) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        if (appointment.getStatus() >= 3) {
            throw new BusinessException("该预约无法取消");
        }
        
        appointment.setStatus(0); // 已取消
        appointment.setCancelReason(reason);
        appointmentMapper.updateById(appointment);
        
        // 更新订单状态
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getAppointmentId, appointmentId);
        Order order = orderMapper.selectOne(wrapper);
        if (order != null) {
            order.setStatus(0); // 已取消
            orderMapper.updateById(order);
        }
    }
    
    /**
     * 获取用户预约列表
     */
    public List<Appointment> getUserAppointments(Long userId) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getUserId, userId)
               .orderByDesc(Appointment::getCreateTime);
        return appointmentMapper.selectList(wrapper);
    }
    
    /**
     * 获取预约详情
     */
    public Appointment getAppointmentDetail(Long appointmentId) {
        return appointmentMapper.selectById(appointmentId);
    }
    
    /**
     * 确认预约（技师/管理员）
     */
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (appointment.getStatus() != 1) {
            throw new BusinessException("预约状态不正确");
        }
        
        appointment.setStatus(2); // 已确认
        appointmentMapper.updateById(appointment);
        
        log.info("预约已确认，ID: {}", appointmentId);
    }
    
    /**
     * 拒绝预约（技师）
     */
    @Transactional
    public void rejectAppointment(Long appointmentId, Long technicianId, String reason) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 验证是否是分配给该技师的预约
        if (appointment.getTechnicianId() != null && !appointment.getTechnicianId().equals(technicianId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        if (appointment.getStatus() != 1) {
            throw new BusinessException("预约状态不正确");
        }
        
        appointment.setStatus(0); // 已取消
        appointment.setCancelReason(reason != null ? reason : "技师拒绝");
        appointmentMapper.updateById(appointment);
        
        // 更新订单状态
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getAppointmentId, appointmentId);
        Order order = orderMapper.selectOne(wrapper);
        if (order != null) {
            order.setStatus(0); // 已取消
            orderMapper.updateById(order);
        }
        
        log.info("预约已拒绝，ID: {}, 原因: {}", appointmentId, reason);
    }
    
    /**
     * 开始服务（技师）
     */
    @Transactional
    public void startService(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 只允许从已确认(2)状态开始服务
        if (appointment.getStatus() != 2) {
            throw new BusinessException("预约未确认，无法开始服务。请先接受预约");
        }
        
        appointment.setStatus(3); // 服务中
        appointmentMapper.updateById(appointment);
        
        log.info("开始服务，预约ID: {}", appointmentId);
    }
    
    /**
     * 完成服务（技师）
     */
    @Transactional
    public void completeService(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (appointment.getStatus() != 3) {
            throw new BusinessException("预约状态不正确，当前状态：" + appointment.getStatus());
        }
        
        // 检查关联订单的支付状态
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getAppointmentId, appointmentId);
        Order order = orderMapper.selectOne(wrapper);
        
        log.info("完成服务 - 预约ID: {}, 订单ID: {}, 订单状态: {}, 支付状态: {}",
                appointmentId, order != null ? order.getId() : "null",
                order != null ? order.getStatus() : "null",
                order != null ? order.getPayStatus() : "null");
        
        if (order != null && order.getPayStatus() == 0) {
            throw new BusinessException("订单尚未支付，请先完成支付");
        }
        
        // 更新预约状态
        appointment.setStatus(4); // 已完成
        appointmentMapper.updateById(appointment);
        log.info("预约状态已更新为完成: {}", appointmentId);
        
        // 更新订单状态 - 使用直接SQL更新确保成功
        if (order != null) {
            try {
                // 方式1：使用updateById
                order.setStatus(3); // 已完成
                int updated = orderMapper.updateById(order);
                log.info("使用updateById更新订单 - 订单ID: {}, 影响行数: {}", order.getId(), updated);
                
                // 如果updateById失败，使用SQL直接更新
                if (updated == 0) {
                    log.warn("updateById返回0，尝试直接SQL更新");
                    orderMapper.update(null,
                        new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Order>()
                            .eq(Order::getId, order.getId())
                            .set(Order::getStatus, 3)
                    );
                    log.info("使用SQL直接更新订单成功 - 订单ID: {}", order.getId());
                }
            } catch (Exception e) {
                log.error("更新订单状态失败，订单ID: {}", order.getId(), e);
                // 尝试使用LambdaUpdateWrapper
                orderMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Order>()
                        .eq(Order::getId, order.getId())
                        .set(Order::getStatus, 3)
                );
                log.info("使用LambdaUpdateWrapper更新订单成功 - 订单ID: {}", order.getId());
            }
        } else {
            log.warn("没有找到关联的订单 - 预约ID: {}", appointmentId);
        }
    }
    
    /**
     * 获取所有预约列表（管理端）
     */
    public List<Appointment> getAllAppointments() {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Appointment::getCreateTime);
        return appointmentMapper.selectList(wrapper);
    }
    
    /**
     * 自动取消已过期的预约
     * 将所有待确认(1)和已确认(2)状态且预约时间已过的预约改为已过期(5)
     */
    @Transactional
    public int cancelExpiredAppointments() {
        // 查询所有待确认和已确认的预约
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Appointment::getStatus, 1, 2) // 待确认、已确认
               .lt(Appointment::getAppointmentTime, LocalDateTime.now()); // 预约时间小于当前时间
        
        List<Appointment> expiredAppointments = appointmentMapper.selectList(wrapper);
        
        if (expiredAppointments.isEmpty()) {
            log.info("没有过期的预约需要处理");
            return 0;
        }
        
        int count = 0;
        for (Appointment appointment : expiredAppointments) {
            // 更新预约状态为已过期(5)
            appointment.setStatus(5); // 已过期
            appointment.setCancelReason("预约时间已过，系统自动取消");
            appointmentMapper.updateById(appointment);
            
            // 更新关联订单状态
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(Order::getAppointmentId, appointment.getId());
            Order order = orderMapper.selectOne(orderWrapper);
            if (order != null && order.getStatus() == 1) { // 只更新待支付的订单
                order.setStatus(0); // 已取消
                orderMapper.updateById(order);
            }
            
            count++;
            log.info("预约已过期，ID: {}, 预约编号: {}, 预约时间: {}", 
                    appointment.getId(), appointment.getAppointmentNo(), appointment.getAppointmentTime());
        }
        
        log.info("共处理 {} 个过期预约", count);
        return count;
    }
}
