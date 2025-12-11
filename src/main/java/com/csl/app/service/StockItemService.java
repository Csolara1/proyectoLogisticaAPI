package com.csl.app.service;

import com.csl.app.model.StockItem;
import java.util.List;
import java.util.Optional;

public interface StockItemService {
    List<StockItem> findAll();
    Optional<StockItem> findById(Long id);
    StockItem save(StockItem stockItem);
    void deleteById(Long id);
}