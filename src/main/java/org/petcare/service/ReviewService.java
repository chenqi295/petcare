package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.Order;
import org.petcare.entity.Review;
import org.petcare.entity.Technician;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.ReviewMapper;
import org.petcare.mapper.TechnicianMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final TechnicianMapper technicianMapper;
    
    /**
     * 创建评价
     */
    @Transactional
    public Review createReview(Review review) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(review.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(review.getUserId())) {
            throw new BusinessException("无权评价此订单");
        }
        
        // 验证订单状态：只有已完成(status=3)且已支付(payStatus=1)的订单才能评价
        if (order.getStatus() != 3) {
            throw new BusinessException("只能评价已完成的订单");
        }
        
        if (order.getPayStatus() != 1) {
            throw new BusinessException("只能评价已支付的订单");
        }
        
        // 检查是否已经评价过
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, review.getOrderId());
        if (reviewMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该订单已评价");
        }
        
        // 验证评分范围
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("评分必须在1-5星之间");
        }
        
        // 如果前端没有传technician_id，从订单中获取
        if (review.getTechnicianId() == null && order.getTechnicianId() != null) {
            review.setTechnicianId(order.getTechnicianId());
            log.info("从订单获取技师ID: orderId={}, technicianId={}", order.getId(), order.getTechnicianId());
        }
        
        review.setStatus(1);
        reviewMapper.insert(review);
        
        log.info("用户 {} 对订单 {} 创建了评价，评分: {}, 技师ID: {}", 
                review.getUserId(), review.getOrderId(), review.getRating(), review.getTechnicianId());
        
        // 更新技师的评分和服务次数
        if (review.getTechnicianId() != null) {
            updateTechnicianRating(review.getTechnicianId());
        } else {
            log.warn("评价没有关联技师ID，无法更新技师评分: reviewId={}", review.getId());
        }
        
        return review;
    }
    
    /**
     * 获取服务的评价列表
     */
    public List<Review> getServiceReviews(Long serviceId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getServiceId, serviceId)
               .eq(Review::getStatus, 1)
               .orderByDesc(Review::getCreateTime);
        return reviewMapper.selectList(wrapper);
    }
    
    /**
     * 获取技师的评价列表
     */
    public List<Review> getTechnicianReviews(Long technicianId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getTechnicianId, technicianId)
               .eq(Review::getStatus, 1)
               .orderByDesc(Review::getCreateTime);
        return reviewMapper.selectList(wrapper);
    }
    
    /**
     * 获取用户的评价列表
     */
    public List<Review> getUserReviews(Long userId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId)
               .orderByDesc(Review::getCreateTime);
        return reviewMapper.selectList(wrapper);
    }
    
    /**
     * 更新技师的评分和服务次数
     */
    private void updateTechnicianRating(Long technicianId) {
        Technician technician = technicianMapper.selectById(technicianId);
        if (technician == null) {
            log.warn("技师不存在: technicianId={}", technicianId);
            return;
        }
        
        // 查询该技师的所有评价
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getTechnicianId, technicianId)
               .eq(Review::getStatus, 1);
        List<Review> reviews = reviewMapper.selectList(wrapper);
        
        // 计算平均评分
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(5.0);
        
        // 四舍五入保留一位小数
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        
        // 更新技师信息
        technician.setRating(averageRating);
        technician.setServiceCount(reviews.size());
        technicianMapper.updateById(technician);
        
        log.info("技师 {} 评分已更新: rating={}, serviceCount={}", 
                technicianId, averageRating, reviews.size());
    }
}
