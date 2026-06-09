package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@TableName("review")
public class Review implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 技师ID
     */
    private Long technicianId;
    
    /**
     * 评分(1-5星)
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片
     */
    private String images;
    
    /**
     * 状态: 0-隐藏, 1-显示
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
