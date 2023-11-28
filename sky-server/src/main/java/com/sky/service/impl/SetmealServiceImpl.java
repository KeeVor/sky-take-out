package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        //首先保存套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        //保存套餐的菜品信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
        setmealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 套餐分类查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页插件
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //创建套餐查询对象
        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealPageQueryDTO.getCategoryId())
                .name(setmealPageQueryDTO.getName())
                .status(setmealPageQueryDTO.getStatus())
                .build();
        Page<SetmealVO> page = setmealMapper.pageQuery(setmeal);
        PageResult pageResult = new PageResult();
        pageResult.setRecords(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    public void update(SetmealDTO setmealDTO) {
        //修改setmeal数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateById(setmeal);
        //删除setmealDish数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //添加setmealDish数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据id获取套餐信息
     * @param id
     * @return
     */
    public SetmealVO queryById(Long id) {
        //获取套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        //获取套餐中的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.queryBySetmealId(id);
        //封装
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        //返回
        return setmealVO;
    }

    /**
     * 套餐起售停售
     * @param status
     */
    public void updateStatus(Integer status,Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.updateById(setmeal);
    }

    /**
     * 批量删除套餐信息
     * @param ids
     */
    public void deleteByIds(List<Long> ids) {
        //批量删除套餐信息
        setmealMapper.deleteByIds(ids);
        //批量删除套餐菜品关联信息
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
