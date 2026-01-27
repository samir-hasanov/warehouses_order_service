package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.PriceCreateRequest;
import www.stock.az.dto.request.PriceUpdateRequest;
import www.stock.az.dto.response.PriceResponse;
import www.stock.az.exception.MyException;
import www.stock.az.service.PriceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1.1/prices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PriceController {
    
    private final PriceService priceService;
    
    @PostMapping
    public ResponseEntity<?> createPrice(@Valid @RequestBody PriceCreateRequest request) {
        try {
            PriceResponse response = priceService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MyException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PriceResponse>> getAllPrices() {
        List<PriceResponse> prices = priceService.findAll();
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPriceById(@PathVariable Long id) {
        try {
            PriceResponse price = priceService.findById(id);
            return ResponseEntity.ok(price);
        } catch (MyException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PriceResponse>> getPricesByProduct(@PathVariable Long productId) {
        List<PriceResponse> prices = priceService.findByProductId(productId);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<PriceResponse>> getPricesByWarehouse(@PathVariable Long warehouseId) {
        List<PriceResponse> prices = priceService.findByWarehouseId(warehouseId);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/product/{productId}/current")
    public ResponseEntity<?> getCurrentPrice(
            @PathVariable Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "SELLING") String priceType) {
        try {
            PriceResponse price = priceService.getCurrentPrice(productId, warehouseId, priceType);
            return ResponseEntity.ok(price);
        } catch (MyException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/product/{productId}/history")
    public ResponseEntity<List<PriceResponse>> getPriceHistory(
            @PathVariable Long productId,
            @RequestParam(required = false) Long warehouseId) {
        List<PriceResponse> prices = priceService.getPriceHistory(productId, warehouseId);
        return ResponseEntity.ok(prices);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceUpdateRequest request) {
        try {
            PriceResponse response = priceService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (MyException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrice(@PathVariable Long id) {
        try {
            priceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (MyException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}

