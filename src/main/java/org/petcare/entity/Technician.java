package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技师实体类
 */
@Data
@TableName("technician")
public class Technician implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 技师姓名
     */
    private String name;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 简介
     */
    private String introduction;
    
    /**
     * 擅长技能
     */
    private String skills;
    
    /**
     * 工作年限
     */
    private Integer workYears;
    
    /**
     * 评分
     */
    private Double rating;
    
    /**
     * 服务次数
     */
    private Integer serviceCount;
    
    /**
     * 状态: 0-休息, 1-工作中, 2-离线
     */
    private Integer status;
    
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
