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
  public Result<List<Coffee>> list() {
    return Result.ok(coffeeService.listAll());
  }

  @GetMapping("/{id}")
  public Result<Coffee> detail(@PathVariable Long id) {
    return Result.ok(coffeeService.findById(id));
  }

  @PostMapping
  public Result<Long> create(@RequestBody Coffee coffee) {
    return Result.ok(coffeeService.create(coffee));
  }

  @PutMapping("/{id}")
  public Result<Void> update(@PathVariable Long id, @RequestBody Coffee coffee) {
    coffee.setId(id);
    coffeeService.update(coffee);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    coffeeService.delete(id);
    return Result.ok();
  }
}
