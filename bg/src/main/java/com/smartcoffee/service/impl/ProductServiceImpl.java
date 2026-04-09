package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Product;
import com.smartcoffee.mapper.ProductMapper;
import com.smartcoffee.mapper.StoreProductMapper;
import com.smartcoffee.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
  @Resource private ProductMapper productMapper;
  @Resource private StoreProductMapper storeProductMapper;

  @Override
  public List<Product> list(Long storeId, Integer categoryId, String keyword, Integer pageNum, Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    if (storeId != null) {
      return storeProductMapper.listStoreProducts(storeId, categoryId, keyword, offset, pageSize);
    }
    return productMapper.list(categoryId, keyword, offset, pageSize);
  }

  @Override
  public int count(Long storeId, Integer categoryId, String keyword) {
    if (storeId != null) {
      return storeProductMapper.countStoreProducts(storeId, categoryId, keyword);
    }
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
    storeProductMapper.initializeMappingsForProduct(product.getId(), product.getIsActive() == null ? 1 : product.getIsActive());
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
