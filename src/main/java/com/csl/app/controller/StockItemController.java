package com.csl.app.controller;

import com.csl.app.model.LogEvent;
import com.csl.app.model.StockItem;
import com.csl.app.repository.LogEventRepository;
import com.csl.app.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*")
public class StockItemController {

    @Autowired
    private StockItemService stockItemService;

    @Autowired
    private LogEventRepository logRepository; // Logs

    @GetMapping
    public List<StockItem> getAllStock() {
        return stockItemService.findAll();
    }

    @PostMapping
    public StockItem createStockItem(@RequestBody StockItem stockItem) {
        StockItem savedItem = stockItemService.save(stockItem);
        
        // Log
        saveLog("INFO", "STOCK", "Entrada de material: " + savedItem.getQuantity() + "x " + savedItem.getProductReference() + " en " + savedItem.getWarehouse());
        
        return savedItem;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable Long id) {
        stockItemService.deleteById(id);
        
        // Log
        saveLog("WARN", "STOCK", "Item de stock eliminado (ID: " + id + ")");
        
        return ResponseEntity.ok().build();
    }

    private void saveLog(String level, String module, String message) {
        LogEvent log = new LogEvent();
        log.setLogLevel(level);
        log.setSourceModule(module);
        log.setEventMessage(message);
        log.setEventTime(LocalDateTime.now());
        logRepository.save(log);
    }
}