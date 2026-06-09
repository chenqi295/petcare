package org.petcare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.Pet;
import org.petcare.service.PetService;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物控制器
 */
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {
    
    private final PetService petService;
    private final JwtUtil jwtUtil;
    
    /**
     * 添加宠物
     */
    @PostMapping("/add")
    public Result<Pet> addPet(@RequestHeader("Authorization") String token,
                              @RequestBody Pet pet) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        pet.setUserId(userId);
        Pet newPet = petService.addPet(pet);
        return Result.success("添加成功", newPet);
    }
    
    /**
     * 更新宠物
     */
    @PutMapping("/update")
    public Result<Pet> updatePet(@RequestHeader("Authorization") String token,
                                 @RequestBody Pet pet) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        pet.setUserId(userId);
        Pet updatedPet = petService.updatePet(pet);
        return Result.success("更新成功", updatedPet);
    }
    
    /**
     * 删除宠物
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePet(@RequestHeader("Authorization") String token,
                                  @PathVariable Long id) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        petService.deletePet(id, userId);
        return Result.successMessage("删除成功");
    }
    
    /**
     * 获取宠物详情
     */
    @GetMapping("/{id}")
    public Result<Pet> getPet(@PathVariable Long id) {
        Pet pet = petService.getPetById(id);
        return Result.success(pet);
    }
    
    /**
     * 获取我的宠物列表
     */
    @GetMapping("/my-pets")
    public Result<List<Pet>> getMyPets(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(actualToken);
        List<Pet> pets = petService.getUserPets(userId);
        return Result.success(pets);
    }
    
    /**
     * 分页查询宠物
     */
    @GetMapping("/page")
    public Result<Page<Pet>> pagePets(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String type) {
        Page<Pet> page = petService.pagePets(pageNum, pageSize, type);
        return Result.success(page);
    }
}
