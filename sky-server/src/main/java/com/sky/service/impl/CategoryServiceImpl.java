package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
        //公共字段注入，已通过aop实现
        //category.setCreateTime(LocalDateTime.now());
        //category.setUpdateTime(LocalDateTime.now());
        //category.setCreateUser(BaseContext.getCurrentId());
        //category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //防止name为空格字符串而导致查不到数据
        if (categoryPageQueryDTO.getName() != null) categoryPageQueryDTO.setName(categoryPageQueryDTO.getName().trim());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        return new PageResult(total,page.getResult());
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //公共字段注入，已通过aop技术实现
        //category.setUpdateUser(BaseContext.getCurrentId());
        //category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
    }

    /**
     * 启用禁用分类状态
     * @param id
     * @param status
     */
    public void updateStatus(long id, int status) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        //公共字段已通过aop实现
        categoryMapper.updateById(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteById(long id) {
        categoryMapper.deleteById(id);
    }


    /**
     * 根据类型查询分类
     * @return
     */
    public List<Category> list(Integer type) {
        Category category = Category.builder()
                .status(StatusConstant.ENABLE)
                .type(type)
                .build();
        return categoryMapper.list(category);
    }
}
