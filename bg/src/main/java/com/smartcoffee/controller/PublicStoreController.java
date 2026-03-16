package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Store;
import com.smartcoffee.mapper.StoreMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class PublicStoreController {
  @Resource private StoreMapper storeMapper;

  @GetMapping
  public Result<List<Store>> listActive() {
    return Result.ok(storeMapper.listActive());
  }
}
