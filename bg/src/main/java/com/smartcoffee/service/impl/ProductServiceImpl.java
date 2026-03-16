package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Product;
import com.smartcoffee.mapper.ProductMapper;
import com.smartcoffee.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
  @Resource private ProductMapper productMapper;

  @Override
  public List<Product> list(Integer categoryId, String keyword, Integer pageNum, Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    return productMapper.list(categoryId, keyword, offset, pageSize);
  }

  @Override
  public int count(Integer categoryId, String keyword) {
    return productMapper.count(categoryId, keyword);
  }

  @Override
  public Product getById(Long id) {
    return productMapper.findById(id);
  }

  @Override
  @Transactional
  public Long create(Product product) {
    productMapper.insert(product);
    return product.getId();
  }

  @Override
  @Transactional
  public void update(Product product) {
    productMapper.update(product);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    productMapper.deleteById(id);
  }
}
