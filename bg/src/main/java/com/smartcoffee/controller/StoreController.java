package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import com.smartcoffee.entity.Product;
import com.smartcoffee.entity.Store;
import com.smartcoffee.entity.StoreProduct;
import com.smartcoffee.mapper.StoreMapper;
import com.smartcoffee.mapper.StoreProductMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/stores")
public class StoreController {
  @Resource private StoreMapper storeMapper;
  @Resource private StoreProductMapper storeProductMapper;
  @Resource private AmapGeocodeClient amapGeocodeClient;

  @PostMapping("/init")
  // 根据历史订单地址初始化门店数据，并尝试自动补全经纬度。
  public Result<Void> initFromOrders() {
    storeMapper.initFromOrders();
    geocodeMissing(50);
    return Result.ok();
  }

  @PostMapping("/geocode-missing")
  // 批量为缺少经纬度的门店调用高德地理编码接口补齐坐标。
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
  // 分页查询后台门店列表。
  public Result<Result.PageData<Store>> list(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Integer offset = (pageNum - 1) * pageSize;
    List<Store> list = storeMapper.list(offset, pageSize);
    int total = storeMapper.count();
    return Result.page(total, list);
  }

  @PostMapping
  // 创建新的门店记录。
  public Result<Long> create(@RequestBody Store store) {
    if (store.getStatus() == null) store.setStatus(1);
    storeMapper.insert(store);
    storeProductMapper.initializeMappingsForStore(store.getId());
    return Result.ok(store.getId());
  }

  @PutMapping("/{id}")
  // 更新指定门店的信息。
  public Result<Void> update(@PathVariable Long id, @RequestBody Store store) {
    store.setId(id);
    storeMapper.update(store);
    return Result.ok();
  }

  @DeleteMapping("/{id}")
  // 删除指定门店。
  public Result<Void> delete(@PathVariable Long id) {
    storeMapper.deleteById(id);
    return Result.ok();
  }

  @GetMapping("/{storeId}/products")
  public Result<Result.PageData<Product>> listStoreProducts(
      @PathVariable Long storeId,
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    if (pageSize > 100) pageSize = 100;
    Integer offset = (pageNum - 1) * pageSize;
    List<Product> list = storeProductMapper.listStoreProductConfigs(storeId, categoryId, keyword, offset, pageSize);
    int total = storeProductMapper.countStoreProductConfigs(storeId, categoryId, keyword);
    return Result.page(total, list);
  }

  @PutMapping("/{storeId}/products/{productId}")
  public Result<Void> updateStoreProductAvailability(
      @PathVariable Long storeId,
      @PathVariable Long productId,
      @RequestBody StoreProduct storeProduct) {
    Integer isAvailable = storeProduct == null ? null : storeProduct.getIsAvailable();
    if (isAvailable == null) {
      return Result.fail(400, "缺少上下架状态");
    }
    storeProductMapper.upsertAvailability(storeId, productId, isAvailable);
    return Result.ok();
  }
}
