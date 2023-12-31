package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 保存新菜品和其口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO queryById(Long id);

    /**
     * 根据id批量删除菜品
     * @param ids
     */
    void removeByIds(List<Long> ids);

    /**
     * 修改菜品数据
     * @param dishVO
     */
    void update(DishVO dishVO);

    /**
     * 菜品起售禁售
     * @param status
     * @param id
     */
    void status(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

}
