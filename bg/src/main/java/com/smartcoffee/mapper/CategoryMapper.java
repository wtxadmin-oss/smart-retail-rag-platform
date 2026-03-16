package com.smartcoffee.mapper;

import com.smartcoffee.entity.Category;
import java.util.List;

public interface CategoryMapper {
  List<Category> list();
  Category findById(Integer id);
  int insert(Category category);
  int update(Category category);
  int deleteById(Integer id);
}
