package org.petcare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物实体类
 */
@Data
@TableName("pet")
public class Pet implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 宠物名称
     */
    private String name;
    
    /**
     * 宠物类型: DOG-狗, CAT-猫, BIRD-鸟, OTHER-其他
     */
    private String type;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 性别: MALE-公, FEMALE-母
     */
    private String gender;
    
    /**
     * 体重(kg)
     */
    private Double weight;
    
    /**
     * 颜色
     */
    private String color;
    
    /**
     * 照片
     */
    private String photo;
    
    /**
     * 健康备注
     */
    private String healthNote;
    
    /**
     * 状态: 0-禁用, 1-正常
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
