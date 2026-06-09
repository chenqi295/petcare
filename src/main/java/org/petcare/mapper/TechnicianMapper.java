package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.Technician;

/**
 * 技师Mapper接口
 */
@Mapper
public interface TechnicianMapper extends BaseMapper<Technician> {
}
