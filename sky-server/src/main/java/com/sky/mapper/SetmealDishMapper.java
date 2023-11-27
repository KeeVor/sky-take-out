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
}
