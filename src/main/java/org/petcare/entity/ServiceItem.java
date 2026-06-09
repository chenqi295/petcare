package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体类
 */
@Data
@TableName("service_item")
public class ServiceItem implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 服务名称
     */
    private String name;
    
    /**
     * 服务描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 时长(分钟)
     */
    private Integer duration;
    
    /**
     * 封面图
     */
    private String coverImage;
    
    /**
     * 详情图片
     */
    private String detailImages;
    
    /**
     * 状态: 0-下架, 1-上架
     */
    private Integer status;
    
    /**
     * 销量
     */
    private Integer sales;
    
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
