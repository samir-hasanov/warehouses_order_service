package www.stock.az.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.PriceCreateRequest;
import www.stock.az.dto.request.PriceUpdateRequest;
import www.stock.az.dto.response.PriceResponse;
import www.stock.az.service.PriceService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/prices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Prices", description = "Price management API endpoints")
public class PriceController {
    
    private final PriceService priceService;
    
    @PostMapping
    @Operation(summary = "Create a new price", description = "Creates a new price for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Price created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PriceResponse> createPrice(@Valid @RequestBody PriceCreateRequest request) {
        try {
            PriceResponse response = priceService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get price by ID", description = "Returns a price by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price found"),
            @ApiResponse(responseCode = "404", description = "Price not found")
    })
    public ResponseEntity<PriceResponse> getPriceById(
            @Parameter(description = "Price ID", required = true) @PathVariable Long id) {
        try {
            PriceResponse price = priceService.findById(id);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get prices by product ID", description = "Returns all prices for a specific product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved prices")
    public ResponseEntity<List<PriceResponse>> getPricesByProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {
        List<PriceResponse> prices = priceService.findByProductId(productId);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get prices by warehouse ID", description = "Returns all prices for a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved prices")
    public ResponseEntity<List<PriceResponse>> getPricesByWarehouse(
            @Parameter(description = "Warehouse ID", required = true) @PathVariable Long warehouseId) {
        List<PriceResponse> prices = priceService.findByWarehouseId(warehouseId);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    @Operation(summary = "Get prices by product and warehouse", description = "Returns prices for a specific product and warehouse")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved prices")
    public ResponseEntity<List<PriceResponse>> getPricesByProductAndWarehouse(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Parameter(description = "Warehouse ID", required = true) @PathVariable Long warehouseId) {
        List<PriceResponse> prices = priceService.findByProductIdAndWarehouseId(productId, warehouseId);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/product/{productId}/current")
    @Operation(summary = "Get current price", description = "Returns the current active price for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current price found"),
            @ApiResponse(responseCode = "404", description = "Current price not found")
    })
    public ResponseEntity<PriceResponse> getCurrentPrice(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Parameter(description = "Warehouse ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Price type", required = true) @RequestParam(defaultValue = "SELLING") String priceType) {
        try {
            PriceResponse price = priceService.getCurrentPrice(productId, warehouseId, priceType);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/product/{productId}/history")
    @Operation(summary = "Get price history", description = "Returns price history for a product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved price history")
    public ResponseEntity<List<PriceResponse>> getPriceHistory(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Parameter(description = "Warehouse ID") @RequestParam(required = false) Long warehouseId) {
        List<PriceResponse> prices = priceService.getPriceHistory(productId, warehouseId);
        return ResponseEntity.ok(prices);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update price", description = "Updates an existing price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price updated successfully"),
            @ApiResponse(responseCode = "404", description = "Price not found")
    })
    public ResponseEntity<PriceResponse> updatePrice(
            @Parameter(description = "Price ID", required = true) @PathVariable Long id,
            @Valid @RequestBody PriceUpdateRequest request) {
        try {
            PriceResponse response = priceService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete price", description = "Deletes a price by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Price deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Price not found")
    })
    public ResponseEntity<Void> deletePrice(
            @Parameter(description = "Price ID", required = true) @PathVariable Long id) {
        try {
            priceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
