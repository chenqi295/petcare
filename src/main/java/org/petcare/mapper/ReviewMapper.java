package org.petcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.petcare.entity.Review;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}
