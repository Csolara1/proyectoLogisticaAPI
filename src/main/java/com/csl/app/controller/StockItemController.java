package com.csl.app.controller;

import com.csl.app.model.StockItem;
import com.csl.app.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
// ❌ BORRADO: @CrossOrigin(origins = "*") para evitar conflicto con SecurityConfig
public class StockItemController {

    @Autowired
    private StockItemService stockItemService;

    @GetMapping
    public List<StockItem> getAllStockItems() {
        return stockItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItem> getStockItemById(@PathVariable Long id) {
        return stockItemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StockItem createStockItem(@RequestBody StockItem stockItem) {
        return stockItemService.save(stockItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItem> updateStockItem(@PathVariable Long id, @RequestBody StockItem stockDetails) {
        return stockItemService.findById(id).map(existing -> {
            existing.setProductReference(stockDetails.getProductReference());
            existing.setWarehouse(stockDetails.getWarehouse());
            existing.setLocation(stockDetails.getLocation());
            existing.setQuantity(stockDetails.getQuantity());
            existing.setUnit(stockDetails.getUnit());
            existing.setLastEntryDate(stockDetails.getLastEntryDate());
            return ResponseEntity.ok(stockItemService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable Long id) {
        if (stockItemService.findById(id).isPresent()) {
            stockItemService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}