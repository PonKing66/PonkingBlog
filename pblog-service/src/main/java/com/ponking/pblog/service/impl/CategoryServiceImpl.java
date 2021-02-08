package com.ponking.pblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ponking.pblog.common.exception.GlobalException;
import com.ponking.pblog.dao.CategoryMapper;
import com.ponking.pblog.model.dto.CategoryAddDTO;
import com.ponking.pblog.model.dto.CategoryEditDTO;
import com.ponking.pblog.model.entity.Category;
import com.ponking.pblog.model.vo.CategoryTableCardVO;
import com.ponking.pblog.service.ICategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 * @author peng
 * @since 2020-03-20
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 插入新的分类
     *
     * @param category
     * @return
     */
    @Override
    public boolean save(CategoryAddDTO category) {
        Category saveCategory = new Category();
        BeanUtils.copyProperties(category, saveCategory);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", category.getName());
        // 1. 判断是否已存在该分类
        Integer count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GlobalException("已存在【" + category.getName() + "】分类");
        }
        return save(saveCategory);
    }

    @Override
    public boolean updateById(CategoryEditDTO editDTO) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        // 1.  originName 为原始名称,updateName为需要更新的名称,若originName = updateName可视为允许修改
        String originName = categoryMapper.selectById(editDTO.getId()).getName();
        String updateName = editDTO.getName();
        Category category = new Category();
        BeanUtils.copyProperties(editDTO, category);
        if (updateName.equals(originName)) {
            return super.updateById(category);
        }
        // 2. 是否已存在该分类(原始的名称可以省略)
        wrapper.eq("name", updateName);
        Integer count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GlobalException("已存在【" + category.getName() + "】分类");
        }
        return super.updateById(category);
    }

    /**
     * 博客左侧栏分类列表
     *
     * @return
     */
    @Override
    public List<CategoryTableCardVO> listCategoryColumnInfo() {
        return categoryMapper.selectCategoryColumnInfo();
    }
}
