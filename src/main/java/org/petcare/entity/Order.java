package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 预约ID
     */
    private Long appointmentId;
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 技师ID
     */
    private Long technicianId;
    
    /**
     * 宠物ID
     */
    private Long petId;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 支付状态: 0-未支付, 1-已支付, 2-已退款
     */
    private Integer payStatus;
    
    /**
     * 支付方式: ALIPAY-支付宝, WECHAT-微信, CASH-现金
     */
    private String payMethod;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime payTime;
    
    /**
     * 订单状态: 0-已取消, 1-待支付, 2-已支付, 3-已完成
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
