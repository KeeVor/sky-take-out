package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 保存新菜品和其口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO) {
        //创建Dish对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //获取新菜品的id
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //保存菜品口味数据
        if (flavors != null && !flavors.isEmpty()){
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
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
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
     * @param id
     * @return
     */
    public DishVO queryById(int id) {
        DishVO dishVO = dishMapper.queryById(id);
        //查询口味信息
        List<DishFlavor> list = dishFlavorMapper.queryByDishId(id);
        //封装口味信息
        dishVO.setFlavors(list);
        return dishVO;
    }
}
