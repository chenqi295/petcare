package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.common.Result;
import org.petcare.entity.Appointment;
import org.petcare.entity.ServiceItem;
import org.petcare.mapper.ServiceItemMapper;
import org.petcare.service.AppointmentService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约控制器
 */
@Slf4j
@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final JwtUtil jwtUtil;
    private final ServiceItemMapper serviceItemMapper;
    
    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result<Appointment> createAppointment(@RequestHeader("Authorization") String token,
                                                 @RequestBody Appointment appointment) {
        try {
            log.info("开始创建预约，参数: {}", appointment);
            
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(actualToken);
            log.info("用户ID: {}", userId);
            
            appointment.setUserId(userId);
            
            Appointment newAppointment = appointmentService.createAppointment(appointment);
            log.info("预约创建成功，ID: {}", newAppointment.getId());
            
            return Result.success("预约成功", newAppointment);
        } catch (Exception e) {
            log.error("创建预约失败", e);
            throw e; // 让全局异常处理器处理
        }
    }
    
    /**
     * 取消预约
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelAppointment(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id,
                                          @RequestParam(required = false) String reason) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        appointmentService.cancelAppointment(id, userId, reason);
        return Result.successMessage("取消成功");
    }
    
    /**
     * 获取我的预约列表
     */
    @GetMapping("/my-appointments")
    public Result<List<Appointment>> getMyAppointments(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        List<Appointment> appointments = appointmentService.getUserAppointments(userId);
        return Result.success(appointments);
    }
    
    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointmentDetail(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentDetail(id);
        return Result.success(appointment);
    }
    
    /**
     * 确认预约（技师/管理员）
     */
    @PostMapping("/confirm/{id}")
    public Result<Void> confirmAppointment(@PathVariable Long id) {
        appointmentService.confirmAppointment(id);
        return Result.successMessage("确认成功");
    }
    
    /**
     * 拒绝预约（技师）
     */
    @PostMapping("/reject/{id}")
    public Result<Void> rejectAppointment(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id,
                                          @RequestParam(required = false) String reason) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        appointmentService.rejectAppointment(id, userId, reason);
        return Result.successMessage("已拒绝预约");
    }
    
    /**
     * 开始服务（技师）
     */
    @PostMapping("/start/{id}")
    public Result<Void> startService(@PathVariable Long id) {
        appointmentService.startService(id);
        return Result.successMessage("服务已开始");
    }
    
    /**
     * 完成服务（技师）
     */
    @PostMapping("/complete/{id}")
    public Result<Void> completeService(@PathVariable Long id) {
        appointmentService.completeService(id);
        return Result.successMessage("服务已完成");
    }
    
    /**
     * 获取所有预约列表（管理端）
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        
        // 为每个预约添加服务名称
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
}
