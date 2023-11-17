package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 保存新菜品和其口味
     *
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO) {
        //创建Dish对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //获取新菜品的id
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //保存菜品口味数据
        if (flavors != null && !flavors.isEmpty()) {
            //给菜品口味添加菜品id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            //批量保存口味
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //分页插件
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //创建菜品对象
        Dish dish = Dish.builder()
                .categoryId(dishPageQueryDTO.getCategoryId())
                .name(dishPageQueryDTO.getName())
                .status(dishPageQueryDTO.getStatus())
                .build();
        //查询到菜品信息
        Page<DishVO> page = dishMapper.pageQuery(dish);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    public DishVO queryById(Long id) {
        DishVO dishVO = dishMapper.queryById(id);
        //查询口味信息
        List<DishFlavor> list = dishFlavorMapper.queryByDishId(id);
        //封装口味信息
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */
    @Transactional
    public void removeByIds(List<Long> ids) {
        //首先查询是否为起售状态，此状态下不能删除。
        for (Long id : ids) {
            DishVO dishVO = dishMapper.queryById(id);
            if (Objects.equals(dishVO.getStatus(), StatusConstant.ENABLE)) {
                //起售状态，不能删除。
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //查询有没有套餐与此菜品关联，如果有，则不能删除菜品。
            SetmealDish setmealDish = setmealDishMapper.queryByDishId(id);
            if (setmealDish != null) {
                //存在相关联的套餐，不能删除。
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除菜品。
        dishMapper.deleteByIds(ids);
        //删除菜品口味。
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 修改菜品数据
     *
     * @param dishVO
     */
    public void update(DishVO dishVO) {
        //创建数据库对应的实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishVO, dish);
        //修改菜品信息
        dishMapper.updateById(dish);
        //删除口味口味信息
        Long dishId = dish.getId();
        //这里直接调用批量删除菜品口味的mapper方法
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dishId));
        //保存菜品口味数据
        List<DishFlavor> flavors = dishVO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //给菜品口味添加菜品id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //批量保存口味
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品起售禁售
     *
     * @param status
     * @param id
     */
    public void status(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateById(dish);
    }
}
