package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.ServiceCategory;

/**
 * 服务分类Mapper
 */
@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {
}
