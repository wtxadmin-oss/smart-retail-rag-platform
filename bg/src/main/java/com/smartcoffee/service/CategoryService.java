package com.smartcoffee.service;

import com.smartcoffee.entity.Category;
import java.util.List;

public interface CategoryService {
  List<Category> list();
  Category findById(Integer id);
  Integer create(Category category);
  void update(Category category);
  void delete(Integer id);
}
