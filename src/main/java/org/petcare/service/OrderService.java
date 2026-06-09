package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.Order;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderMapper orderMapper;
    
    /**
     * 获取用户订单列表
     */
    public List<Order> getUserOrders(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
    
    /**
     * 获取订单详情
     */
    public Order getOrderDetail(Long orderId) {
        return orderMapper.selectById(orderId);
    }
    
    /**
     * 模拟支付
     */
    public void payOrder(Long orderId, String payMethod) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 允许待支付(1)、已支付(2)、服务中(3)的订单进行支付
        if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 记录支付信息
        String payMethodName = getPayMethodName(payMethod);
        log.info("========== 模拟支付开始 ==========");
        log.info("订单号: {}", order.getOrderNo());
        log.info("订单金额: ¥{}", order.getAmount());
        log.info("支付方式: {} ({})", payMethodName, payMethod);
        log.info("用户ID: {}", order.getUserId());
        
        // 模拟支付处理延迟（1-2秒）
        try {
            Thread.sleep(1000 + (long)(Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 更新订单状态
        order.setPayStatus(1); // 已支付
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());
        order.setStatus(2); // 已支付
        
        orderMapper.updateById(order);
        
        log.info("支付成功！支付时间: {}", order.getPayTime());
        log.info("========== 模拟支付完成 ==========");
    }
    
    /**
     * 获取支付方式中文名称
     */
    private String getPayMethodName(String payMethod) {
        switch (payMethod) {
            case "ALIPAY":
                return "支付宝";
            case "WECHAT":
                return "微信支付";
            case "BANK_CARD":
                return "银行卡";
            default:
                return "未知方式";
        }
    }
    
    /**
     * 取消订单
     */
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getPayStatus() == 1) {
            throw new BusinessException("已支付订单无法取消，请申请退款");
        }
        
        order.setStatus(0); // 已取消
        orderMapper.updateById(order);
    }
    
    /**
     * 获取所有订单列表（管理端）
     */
    public List<Order> getAllOrders() {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
}
