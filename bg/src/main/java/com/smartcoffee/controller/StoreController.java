package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Store;
import com.smartcoffee.mapper.StoreMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/stores")
public class StoreController {
  @Resource private StoreMapper storeMapper;
  @Resource private AmapGeocodeClient amapGeocodeClient;

  @PostMapping("/init")
  public Result<Void> initFromOrders() {
    storeMapper.initFromOrders();
    geocodeMissing(50);
    return Result.ok();
  }

  @PostMapping("/geocode-missing")
  public Result<Void> geocodeMissing(@RequestParam(defaultValue = "50") Integer limit) {
    List<Store> need = storeMapper.listNeedGeo(limit);
    for (Store s : need) {
      AmapGeocodeClient.LngLat ll = amapGeocodeClient.geocode(s.getAddress());
      if (ll != null) {
        storeMapper.updateGeo(s.getId(), ll.lng, ll.lat);
      }
    }
    return Result.ok();
  }

  @GetMapping
  public Result<Result.PageData<Store>> list(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    List<Store> list = storeMapper.list(offset, pageSize);
    int total = storeMapper.count();
    return Result.page(total, list);
  }

  @PostMapping
  public Result<Long> create(@RequestBody Store store) {
    if (store.getStatus() == null) store.setStatus(1);
    storeMapper.insert(store);
    return Result.ok(store.getId());
  }

  @PutMapping("/{id}")
  public Result<Void> update(@PathVariable Long id, @RequestBody Store store) {
    store.setId(id);
    storeMapper.update(store);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    storeMapper.deleteById(id);
    return Result.ok();
  }
}
