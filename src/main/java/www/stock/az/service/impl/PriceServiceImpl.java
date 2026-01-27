package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.PriceCreateRequest;
import www.stock.az.dto.request.PriceUpdateRequest;
import www.stock.az.dto.response.PriceResponse;
import www.stock.az.entity.Price;
import www.stock.az.exception.MyException;
import www.stock.az.repository.PriceRepository;
import www.stock.az.service.PriceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {
    
    private final PriceRepository priceRepository;
    
    @Override
    @Transactional
    public PriceResponse create(PriceCreateRequest request) {
        // Check if price already exists for this product, warehouse, and price type
        if (request.getWarehouseId() != null) {
            priceRepository.findByProductIdAndWarehouseIdAndPriceType(
                request.getProductId(), 
                request.getWarehouseId(), 
                request.getPriceType()
            ).ifPresent(existing -> {
                throw new MyException("Price already exists for this product, warehouse, and price type");
            });
        } else {
            // Check for global price (warehouseId is null)
            priceRepository.findGlobalPriceByProductIdAndPriceType(
                request.getProductId(), 
                request.getPriceType()
            ).ifPresent(existing -> {
                throw new MyException("Global price already exists for this product and price type");
            });
        }
        
        Price price = new Price();
        price.setProductId(request.getProductId());
        price.setWarehouseId(request.getWarehouseId());
        price.setPriceType(request.getPriceType());
        price.setUnitPrice(request.getUnitPrice());
        price.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        price.setMinQuantity(request.getMinQuantity());
        price.setValidFrom(request.getValidFrom() != null ? request.getValidFrom() : LocalDateTime.now());
        price.setValidTo(request.getValidTo());
        price.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        price.setNotes(request.getNotes());
        
        Price saved = priceRepository.save(price);
        return mapToResponse(saved);
    }
    
    @Override
    public PriceResponse findById(Long id) {
        Price price = priceRepository.findById(id)
            .orElseThrow(() -> new MyException("Price not found with id: " + id));
        return mapToResponse(price);
    }
    
    @Override
    public List<PriceResponse> findAll() {
        return priceRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PriceResponse> findByProductId(Long productId) {
        return priceRepository.findByProductId(productId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PriceResponse> findByWarehouseId(Long warehouseId) {
        return priceRepository.findByWarehouseId(warehouseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public PriceResponse getCurrentPrice(Long productId, Long warehouseId, String priceType) {
        LocalDateTime now = LocalDateTime.now();
        Price price = priceRepository.findCurrentPrice(productId, warehouseId, priceType, now)
            .orElseThrow(() -> new MyException("Current price not found for product: " + productId + 
                ", warehouse: " + warehouseId + ", priceType: " + priceType));
        return mapToResponse(price);
    }
    
    @Override
    public List<PriceResponse> getPriceHistory(Long productId, Long warehouseId) {
        return priceRepository.findPriceHistory(productId, warehouseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PriceResponse update(Long id, PriceUpdateRequest request) {
        Price price = priceRepository.findById(id)
            .orElseThrow(() -> new MyException("Price not found with id: " + id));
        
        if (request.getWarehouseId() != null) {
            price.setWarehouseId(request.getWarehouseId());
        }
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
    @Transactional
    public void delete(Long id) {
        if (!priceRepository.existsById(id)) {
            throw new MyException("Price not found with id: " + id);
        }
        priceRepository.deleteById(id);
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

