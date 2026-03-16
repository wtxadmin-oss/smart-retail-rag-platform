package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Category;
import com.smartcoffee.mapper.CategoryMapper;
import com.smartcoffee.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
  @Resource private CategoryMapper categoryMapper;

  @Override
  public List<Category> list() {
    return categoryMapper.list();
  }

  @Override
  public Category findById(Integer id) {
    return categoryMapper.findById(id);
  }

  @Override
  @Transactional
  public Integer create(Category category) {
    categoryMapper.insert(category);
    return category.getId();
  }

  @Override
  @Transactional
  public void update(Category category) {
    categoryMapper.update(category);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    categoryMapper.deleteById(id);
  }
}
