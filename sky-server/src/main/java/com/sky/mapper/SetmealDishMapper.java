package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     *根据菜品id查
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where dish_id = #{id}")
    SetmealDish queryByDishId(Long id);

    /**
     * 批量保存套餐菜品关联信息
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除数据
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐菜品关联信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> queryBySetmealId(Long setmealId);

    /**
     * 根据套餐id批量删除套餐菜品关联信息
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);
}
