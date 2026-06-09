package org.petcare.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.service.AppointmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 预约定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentScheduler {
    
    private final AppointmentService appointmentService;
    
    /**
     * 每分钟检查一次过期预约
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkExpiredAppointments() {
        log.info("开始检查过期预约...");
        try {
            int count = appointmentService.cancelExpiredAppointments();
            if (count > 0) {
                log.info("成功处理 {} 个过期预约", count);
            }
        } catch (Exception e) {
            log.error("检查过期预约失败", e);
        }
    }
}
