package com.smartcoffee.service;

import com.smartcoffee.entity.Product;
import java.util.List;

public interface ProductService {
  List<Product> list(Integer categoryId, String keyword, Integer pageNum, Integer pageSize);
    int count(Integer categoryId, String keyword);
    Product getById(Long id);
  Long create(Product product);
  void update(Product product);
  void delete(Long id);
}
