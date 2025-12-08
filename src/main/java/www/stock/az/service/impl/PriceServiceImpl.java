package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.PriceCreateRequest;
import www.stock.az.dto.request.PriceUpdateRequest;
import www.stock.az.dto.response.PriceResponse;
import www.stock.az.entity.Price;
import www.stock.az.repository.PriceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceServiceImpl implements www.stock.az.service.PriceService {
    
    private final PriceRepository priceRepository;
    
    @Override
    public PriceResponse create(PriceCreateRequest request) {
        Price price = new Price();
        price.setProductId(request.getProductId());
        price.setWarehouseId(request.getWarehouseId());
        price.setPriceType(request.getPriceType());
        price.setUnitPrice(request.getUnitPrice());
        price.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        price.setMinQuantity(request.getMinQuantity());
        price.setValidFrom(request.getValidFrom());
        price.setValidTo(request.getValidTo());
        price.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        price.setNotes(request.getNotes());
        
        Price saved = priceRepository.save(price);
        return mapToResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PriceResponse findById(Long id) {
        Price price = priceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Price not found: " + id));
        return mapToResponse(price);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PriceResponse> findByProductId(Long productId) {
        return priceRepository.findByProductId(productId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PriceResponse> findByWarehouseId(Long warehouseId) {
        return priceRepository.findByWarehouseId(warehouseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PriceResponse> findByProductIdAndWarehouseId(Long productId, Long warehouseId) {
        return priceRepository.findByProductIdAndWarehouseId(productId, warehouseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PriceResponse getCurrentPrice(Long productId, Long warehouseId, String priceType) {
        var priceOpt = priceRepository.findCurrentPrice(
            productId, 
            warehouseId, 
            priceType, 
            LocalDateTime.now()
        );
        
        if (priceOpt.isEmpty()) {
            // Try to find warehouse-agnostic price
            var prices = priceRepository.findCurrentPrices(productId, warehouseId, priceType, LocalDateTime.now());
            if (!prices.isEmpty()) {
                return mapToResponse(prices.get(0));
            }
            throw new RuntimeException("Current price not found for product: " + productId);
        }
        
        return mapToResponse(priceOpt.get());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PriceResponse> getPriceHistory(Long productId, Long warehouseId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusYears(1); // Last year
        
        return priceRepository.findPriceHistory(productId, warehouseId, startDate, endDate).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public PriceResponse update(Long id, PriceUpdateRequest request) {
        Price price = priceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Price not found: " + id));
        
        if (request.getPriceType() != null) {
            price.setPriceType(request.getPriceType());
        }
        if (request.getUnitPrice() != null) {
            price.setUnitPrice(request.getUnitPrice());
        }
        if (request.getCurrency() != null) {
            price.setCurrency(request.getCurrency());
        }
        if (request.getMinQuantity() != null) {
            price.setMinQuantity(request.getMinQuantity());
        }
        if (request.getValidFrom() != null) {
            price.setValidFrom(request.getValidFrom());
        }
        if (request.getValidTo() != null) {
            price.setValidTo(request.getValidTo());
        }
        if (request.getIsActive() != null) {
            price.setIsActive(request.getIsActive());
        }
        if (request.getNotes() != null) {
            price.setNotes(request.getNotes());
        }
        
        Price updated = priceRepository.save(price);
        return mapToResponse(updated);
    }
    
    @Override
    public void delete(Long id) {
        Price price = priceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Price not found: " + id));
        priceRepository.delete(price);
    }
    
    private PriceResponse mapToResponse(Price price) {
        PriceResponse response = new PriceResponse();
        response.setId(price.getId());
        response.setProductId(price.getProductId());
        response.setWarehouseId(price.getWarehouseId());
        response.setPriceType(price.getPriceType());
        response.setUnitPrice(price.getUnitPrice());
        response.setCurrency(price.getCurrency());
        response.setMinQuantity(price.getMinQuantity());
        response.setValidFrom(price.getValidFrom());
        response.setValidTo(price.getValidTo());
        response.setIsActive(price.getIsActive());
        response.setNotes(price.getNotes());
        response.setCreatedAt(price.getCreatedAt());
        response.setUpdatedAt(price.getUpdatedAt());
        return response;
    }
}
