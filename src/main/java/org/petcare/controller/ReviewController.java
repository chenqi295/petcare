package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.Order;
import org.petcare.entity.Review;
import org.petcare.entity.ServiceItem;
import org.petcare.mapper.OrderMapper;
import org.petcare.mapper.ServiceItemMapper;
import org.petcare.service.ReviewService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;
    private final OrderMapper orderMapper;
    private final ServiceItemMapper serviceItemMapper;
    
    /**
     * 创建评价
     */
    @PostMapping("/create")
    public Result<Review> createReview(@RequestHeader("Authorization") String token,
                                       @RequestBody Review review) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        review.setUserId(userId);
        
        Review newReview = reviewService.createReview(review);
        return Result.success("评价成功", newReview);
    }
    
    /**
     * 获取服务的评价列表
     */
    @GetMapping("/service/{serviceId}")
    public Result<List<Review>> getServiceReviews(@PathVariable Long serviceId) {
        List<Review> reviews = reviewService.getServiceReviews(serviceId);
        return Result.success(reviews);
    }
    
    /**
     * 获取技师的评价列表
     */
    @GetMapping("/technician/{technicianId}")
    public Result<List<Review>> getTechnicianReviews(@PathVariable Long technicianId) {
        List<Review> reviews = reviewService.getTechnicianReviews(technicianId);
        return Result.success(reviews);
    }
    
    /**
     * 获取我的评价列表
     */
    @GetMapping("/my-reviews")
    public Result<List<Map<String, Object>>> getMyReviews(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        List<Review> reviews = reviewService.getUserReviews(userId);
        
        // 为每个评价添加订单号和服务名称
        List<Map<String, Object>> result = reviews.stream().map(review -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", review.getId());
            map.put("userId", review.getUserId());
            map.put("orderId", review.getOrderId());
            map.put("serviceId", review.getServiceId());
            map.put("technicianId", review.getTechnicianId());
            map.put("rating", review.getRating());
            map.put("content", review.getContent());
            map.put("images", review.getImages());
            map.put("status", review.getStatus());
            map.put("createTime", review.getCreateTime());
            map.put("updateTime", review.getUpdateTime());
            
            // 获取订单号
            if (review.getOrderId() != null) {
                Order order = orderMapper.selectById(review.getOrderId());
                map.put("orderNo", order != null ? order.getOrderNo() : "-");
            } else {
                map.put("orderNo", "-");
            }
            
            // 获取服务名称
            if (review.getServiceId() != null) {
                ServiceItem service = serviceItemMapper.selectById(review.getServiceId());
                map.put("serviceName", service != null ? service.getName() : "未知服务");
            } else {
                map.put("serviceName", "-");
            }
            
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
}
