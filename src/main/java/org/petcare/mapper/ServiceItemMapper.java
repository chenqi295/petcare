package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.ServiceItem;

/**
 * 服务项目Mapper
 */
@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {
}
