package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.Appointment;

/**
 * 预约Mapper
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
