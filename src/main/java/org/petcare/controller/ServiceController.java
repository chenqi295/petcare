package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.ServiceCategory;
import org.petcare.entity.ServiceItem;
import org.petcare.service.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务控制器
 */
@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {
    
    private final ServiceService serviceService;
    
    /**
     * 获取所有服务分类
     */
    @GetMapping("/categories")
    public Result<List<ServiceCategory>> getCategories() {
        List<ServiceCategory> categories = serviceService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 获取分类下的服务项目
     */
    @GetMapping("/items/{categoryId}")
    public Result<List<ServiceItem>> getServiceItems(@PathVariable Long categoryId) {
        List<ServiceItem> items = serviceService.getServiceItemsByCategory(categoryId);
        return Result.success(items);
    }
    
    /**
     * 获取热门服务
     */
    @GetMapping("/hot")
    public Result<List<ServiceItem>> getHotServices(@RequestParam(defaultValue = "6") Integer limit) {
        List<ServiceItem> hotServices = serviceService.getHotServices(limit);
        return Result.success(hotServices);
    }
    
    /**
     * 获取服务详情
     */
    @GetMapping("/detail/{id}")
    public Result<ServiceItem> getServiceDetail(@PathVariable Long id) {
        ServiceItem item = serviceService.getServiceDetail(id);
        if (item == null) {
            return Result.error("服务不存在或已下架");
        }
        return Result.success(item);
    }
    
    /**
     * 获取所有服务项目（管理端）
     */
    @GetMapping("/items/all")
    public Result<List<ServiceItem>> getAllServiceItems() {
        List<ServiceItem> items = serviceService.getAllServiceItems();
        return Result.success(items);
    }
    
    /**
     * 更新服务项目状态（上架/下架）
     */
    @PutMapping("/items/{id}/status")
    public Result<ServiceItem> updateServiceItemStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        ServiceItem item = serviceService.updateServiceItemStatus(id, request.getStatus());
        return Result.success("更新成功", item);
    }
    
    /**
     * 创建服务项目
     */
    @PostMapping("/items")
    public Result<ServiceItem> createServiceItem(@RequestBody ServiceItem serviceItem) {
        ServiceItem item = serviceService.createServiceItem(serviceItem);
        return Result.success("创建成功", item);
    }
    
    /**
     * 创建服务分类
     */
    @PostMapping("/categories")
    public Result<ServiceCategory> createCategory(@RequestBody ServiceCategory category) {
        ServiceCategory newCategory = serviceService.createCategory(category);
        return Result.success("创建成功", newCategory);
    }
    
    /**
     * 删除服务分类
     */
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        serviceService.deleteCategory(id);
        return Result.successMessage("删除成功");
    }
    
    /**
     * 删除服务项目
     */
    @DeleteMapping("/items/{id}")
    public Result<Void> deleteServiceItem(@PathVariable Long id) {
        serviceService.deleteServiceItem(id);
        return Result.successMessage("删除成功");
    }
    
    // 请求DTO类
    static class UpdateStatusRequest {
        private Integer status;
        
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
}
