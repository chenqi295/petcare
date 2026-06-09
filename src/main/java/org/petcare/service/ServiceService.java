package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.ServiceCategory;
import org.petcare.entity.ServiceItem;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.ServiceCategoryMapper;
import org.petcare.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceService {
    
    private final ServiceCategoryMapper categoryMapper;
    private final ServiceItemMapper itemMapper;
    
    /**
     * 获取所有服务分类
     */
    public List<ServiceCategory> getAllCategories() {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getStatus, 1)
               .orderByAsc(ServiceCategory::getSort);
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 获取分类下的服务项目
     */
    public List<ServiceItem> getServiceItemsByCategory(Long categoryId) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getCategoryId, categoryId)
               .eq(ServiceItem::getStatus, 1)
               .orderByDesc(ServiceItem::getSales);
        return itemMapper.selectList(wrapper);
    }
    
    /**
     * 获取热门服务
     */
    public List<ServiceItem> getHotServices(Integer limit) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getStatus, 1)
               .orderByDesc(ServiceItem::getSales)
               .last("LIMIT " + limit);
        return itemMapper.selectList(wrapper);
    }
    
    /**
     * 获取服务详情
     */
    public ServiceItem getServiceDetail(Long serviceId) {
        ServiceItem item = itemMapper.selectById(serviceId);
        if (item != null && item.getStatus() == 0) {
            return null;
        }
        return item;
    }
    
    /**
     * 获取所有服务项目（管理端）
     */
    public List<ServiceItem> getAllServiceItems() {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ServiceItem::getCreateTime);
        return itemMapper.selectList(wrapper);
    }
    
    /**
     * 更新服务项目状态（上架/下架）
     */
    public ServiceItem updateServiceItemStatus(Long id, Integer status) {
        ServiceItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("服务项目不存在");
        }
        item.setStatus(status);
        itemMapper.updateById(item);
        return item;
    }
    
    /**
     * 创建服务项目
     */
    public ServiceItem createServiceItem(ServiceItem serviceItem) {
        // 验证分类是否存在
        ServiceCategory category = categoryMapper.selectById(serviceItem.getCategoryId());
        if (category == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 设置默认值
        if (serviceItem.getStatus() == null) {
            serviceItem.setStatus(1); // 默认上架
        }
        if (serviceItem.getSales() == null) {
            serviceItem.setSales(0); // 默认销量为0
        }
        
        itemMapper.insert(serviceItem);
        return serviceItem;
    }
    
    /**
     * 创建服务分类
     */
    public ServiceCategory createCategory(ServiceCategory category) {
        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1); // 默认启用
        }
        
        categoryMapper.insert(category);
        return category;
    }
    
    /**
     * 删除服务分类
     */
    public void deleteCategory(Long id) {
        ServiceCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 检查是否有服务项目使用该分类
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getCategoryId, id);
        long count = itemMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该分类下还有服务项目，无法删除");
        }
        
        categoryMapper.deleteById(id);
    }
    
    /**
     * 删除服务项目
     */
    public void deleteServiceItem(Long id) {
        ServiceItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        itemMapper.deleteById(id);
    }
}
