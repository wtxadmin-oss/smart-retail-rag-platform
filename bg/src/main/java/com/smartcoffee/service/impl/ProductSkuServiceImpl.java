package com.smartcoffee.service.impl;

import com.smartcoffee.entity.ProductSku;
import com.smartcoffee.mapper.ProductSkuMapper;
import com.smartcoffee.service.ProductSkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductSkuServiceImpl implements ProductSkuService {
  @Resource private ProductSkuMapper productSkuMapper;

  @Override
  public List<ProductSku> listByProductId(Long productId) {
    return productSkuMapper.listByProductId(productId);
  }

  @Override
  public ProductSku findById(Long id) {
    return productSkuMapper.findById(id);
  }

  @Override
  @Transactional
  public Long create(ProductSku sku) {
    productSkuMapper.insert(sku);
    return sku.getId();
  }

  @Override
  @Transactional
  public void update(ProductSku sku) {
    productSkuMapper.update(sku);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    productSkuMapper.deleteById(id);
  }
}
