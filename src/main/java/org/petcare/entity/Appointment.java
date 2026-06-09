package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 预约编号
     */
    private String appointmentNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 宠物ID
     */
    private Long petId;
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 技师ID
     */
    private Long technicianId;
    
    /**
     * 预约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime appointmentTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 状态: 0-已取消, 1-待确认, 2-已确认, 3-服务中, 4-已完成
     */
    private Integer status;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
