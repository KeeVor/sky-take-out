package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 保存菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dish
     * @return
     */
    Page<DishVO> pageQuery(Dish dish);

    /**
     * 根据菜品id查询
     * @param id
     * @return
     */
    @Select("select d.*,c.`name` as category_name from dish d left outer join category c on d.category_id = c.id" +
            " where d.id = #{id}")
    DishVO queryById(Long id);


    /**
     * 根据菜品id批量删除
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据菜品id修改菜品信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateById(Dish dish);
}
