package com.smartcoffee.service.impl;

import com.smartcoffee.entity.Coffee;
import com.smartcoffee.mapper.CoffeeMapper;
import com.smartcoffee.service.CoffeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class CoffeeServiceImpl implements CoffeeService {
  @Resource private CoffeeMapper coffeeMapper;

  @Override
  public List<Coffee> listAll() {
    return coffeeMapper.listAll();
  }

  @Override
  public Coffee findById(Long id) {
    return coffeeMapper.findById(id);
  }

  @Override
  @Transactional
  public Long create(Coffee coffee) {
    coffeeMapper.insert(coffee);
    return coffee.getId();
  }

  @Override
  @Transactional
  public void update(Coffee coffee) {
    coffeeMapper.update(coffee);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    coffeeMapper.deleteById(id);
  }
}
