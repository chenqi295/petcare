package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.Pet;

/**
 * 宠物Mapper
 */
@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
