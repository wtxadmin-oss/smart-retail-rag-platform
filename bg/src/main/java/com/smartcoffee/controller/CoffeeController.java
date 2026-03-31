package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Coffee;
import com.smartcoffee.service.CoffeeService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {
  @Resource private CoffeeService coffeeService;

  @GetMapping
  // 查询全部咖啡数据列表。
  public Result<List<Coffee>> list() {
    return Result.ok(coffeeService.listAll());
  }

  @GetMapping("/{id}")
  // 根据 ID 查询单条咖啡记录。
  public Result<Coffee> detail(@PathVariable Long id) {
    return Result.ok(coffeeService.findById(id));
  }

  @PostMapping
  // 新建咖啡数据。
  public Result<Long> create(@RequestBody Coffee coffee) {
    return Result.ok(coffeeService.create(coffee));
  }

  @PutMapping("/{id}")
  // 更新指定咖啡数据。
  public Result<Void> update(@PathVariable Long id, @RequestBody Coffee coffee) {
    coffee.setId(id);
    coffeeService.update(coffee);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  // 删除指定咖啡数据。
  public Result<Void> delete(@PathVariable Long id) {
    coffeeService.delete(id);
    return Result.ok();
  }
}
