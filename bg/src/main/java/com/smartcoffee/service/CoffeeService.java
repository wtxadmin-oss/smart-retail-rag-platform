package com.smartcoffee.service;

import com.smartcoffee.entity.Coffee;
import java.util.List;

public interface CoffeeService {
  List<Coffee> listAll();
  Coffee findById(Long id);
  Long create(Coffee coffee);
  void update(Coffee coffee);
  void delete(Long id);
}
