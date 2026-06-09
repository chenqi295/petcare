package org.petcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.petcare.entity.Pet;
import org.petcare.exception.BusinessException;
import org.petcare.mapper.PetMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {
    
    private final PetMapper petMapper;
    
    /**
     * 添加宠物
     */
    public Pet addPet(Pet pet) {
        pet.setStatus(1);
        petMapper.insert(pet);
        return pet;
    }
    
    /**
     * 更新宠物信息
     */
    public Pet updatePet(Pet pet) {
        Pet existingPet = petMapper.selectById(pet.getId());
        if (existingPet == null) {
            throw new BusinessException("宠物不存在");
        }
        
        // 验证权限
        if (!existingPet.getUserId().equals(pet.getUserId())) {
            throw new BusinessException("无权操作此宠物");
        }
        
        petMapper.updateById(pet);
        return petMapper.selectById(pet.getId());
    }
    
    /**
     * 删除宠物
     */
    public void deletePet(Long petId, Long userId) {
        Pet pet = petMapper.selectById(petId);
        if (pet == null) {
            throw new BusinessException("宠物不存在");
        }
        
        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此宠物");
        }
        
        petMapper.deleteById(petId);
    }
    
    /**
     * 获取宠物详情
     */
    public Pet getPetById(Long petId) {
        return petMapper.selectById(petId);
    }
    
    /**
     * 获取用户的宠物列表
     */
    public List<Pet> getUserPets(Long userId) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getUserId, userId)
               .eq(Pet::getStatus, 1)
               .orderByDesc(Pet::getCreateTime);
        return petMapper.selectList(wrapper);
    }
    
    /**
     * 分页查询宠物
     */
    public Page<Pet> pagePets(Integer pageNum, Integer pageSize, String type) {
        Page<Pet> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getStatus, 1);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Pet::getType, type);
        }
        
        wrapper.orderByDesc(Pet::getCreateTime);
        return petMapper.selectPage(page, wrapper);
    }
}
