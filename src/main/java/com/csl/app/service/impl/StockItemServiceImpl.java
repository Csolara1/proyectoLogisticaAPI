package com.csl.app.service.impl;

import com.csl.app.model.StockItem;
import com.csl.app.repository.StockItemRepository;
import com.csl.app.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockItemServiceImpl implements StockItemService {

    @Autowired
    private StockItemRepository stockItemRepository;

    @Override
    public List<StockItem> findAll() {
        return stockItemRepository.findAll();
    }

    @Override
    public Optional<StockItem> findById(Long id) {
        return stockItemRepository.findById(id);
    }

    @Override
    public StockItem save(StockItem stockItem) {
        return stockItemRepository.save(stockItem);
    }

    @Override
    public void deleteById(Long id) {
        stockItemRepository.deleteById(id);
    }
}