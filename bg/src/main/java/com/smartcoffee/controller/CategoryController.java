package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Category;
import com.smartcoffee.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  @Resource private CategoryService categoryService;

  @GetMapping
  // 查询全部商品分类列表。
  public Result<List<Category>> list() {
    return Result.ok(categoryService.list());
  }

  @GetMapping("/{id}")
  // 根据分类 ID 查询单个分类详情。
  public Result<Category> detail(@PathVariable Integer id) {
    return Result.ok(categoryService.findById(id));
  }

  @PostMapping
  // 新建商品分类。
  public Result<Integer> create(@RequestBody Category category) {
    return Result.ok(categoryService.create(category));
  }

  @PutMapping("/{id}")
  // 更新指定商品分类。
  public Result<Void> update(@PathVariable Integer id, @RequestBody Category category) {
    category.setId(id);
    categoryService.update(category);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  // 删除指定商品分类。
  public Result<Void> delete(@PathVariable Integer id) {
    categoryService.delete(id);
    return Result.ok();
  }
}
