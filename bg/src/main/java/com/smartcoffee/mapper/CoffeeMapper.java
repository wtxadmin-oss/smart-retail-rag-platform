package com.smartcoffee.mapper;

import com.smartcoffee.entity.Coffee;
import java.util.List;

public interface CoffeeMapper {
    List<Coffee> listAll();
    Coffee findById(Long id);
    int insert(Coffee coffee);
    int update(Coffee coffee);
    int deleteById(Long id);
}
