package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.DiscountCreateRequest;
import www.stock.az.dto.request.DiscountUpdateRequest;
import www.stock.az.dto.response.DiscountResponse;
import www.stock.az.entity.Discount;
import www.stock.az.repository.DiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements www.stock.az.service.DiscountService {
    
    private final DiscountRepository discountRepository;
    
    @Override
    public DiscountResponse create(DiscountCreateRequest request) {
        if (discountRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Discount code already exists: " + request.getCode());
        }
        
        Discount discount = new Discount();
        discount.setCode(request.getCode());
        discount.setName(request.getName());
        discount.setDescription(request.getDescription());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setMinPurchaseAmount(request.getMinPurchaseAmount());
        discount.setMaxDiscountAmount(request.getMaxDiscountAmount());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setUsageLimit(request.getUsageLimit());
        discount.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        discount.setApplicableToAllProducts(request.getApplicableToAllProducts() != null ? 
            request.getApplicableToAllProducts() : true);
        discount.setProductIds(request.getProductIds());
        discount.setCustomerIds(request.getCustomerIds());
        discount.setUsageCount(0);
        
        Discount saved = discountRepository.save(discount);
        return mapToResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DiscountResponse findById(Long id) {
        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found: " + id));
        return mapToResponse(discount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DiscountResponse findByCode(String code) {
        Discount discount = discountRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Discount not found: " + code));
        return mapToResponse(discount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> findAll() {
        return discountRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> findActive() {
        return discountRepository.findActiveDiscounts(LocalDateTime.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> search(String query) {
        return discountRepository.searchDiscounts(query).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public DiscountResponse update(Long id, DiscountUpdateRequest request) {
        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found: " + id));
        
        if (request.getName() != null) {
            discount.setName(request.getName());
        }
        if (request.getDescription() != null) {
            discount.setDescription(request.getDescription());
        }
        if (request.getDiscountType() != null) {
            discount.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            discount.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinPurchaseAmount() != null) {
            discount.setMinPurchaseAmount(request.getMinPurchaseAmount());
        }
        if (request.getMaxDiscountAmount() != null) {
            discount.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (request.getStartDate() != null) {
            discount.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            discount.setEndDate(request.getEndDate());
        }
        if (request.getUsageLimit() != null) {
            discount.setUsageLimit(request.getUsageLimit());
        }
        if (request.getIsActive() != null) {
            discount.setIsActive(request.getIsActive());
        }
        if (request.getApplicableToAllProducts() != null) {
            discount.setApplicableToAllProducts(request.getApplicableToAllProducts());
        }
        if (request.getProductIds() != null) {
            discount.setProductIds(request.getProductIds());
        }
        if (request.getCustomerIds() != null) {
            discount.setCustomerIds(request.getCustomerIds());
        }
        
        Discount updated = discountRepository.save(discount);
        return mapToResponse(updated);
    }
    
    @Override
    public void delete(Long id) {
        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found: " + id));
        discountRepository.delete(discount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DiscountResponse validate(String code, BigDecimal orderAmount) {
        var discountOpt = discountRepository.findActiveDiscountByCode(code, LocalDateTime.now());
        
        if (discountOpt.isEmpty()) {
            return null;
        }
        
        Discount discount = discountOpt.get();
        
        // Check minimum purchase amount
        if (discount.getMinPurchaseAmount() != null && 
            orderAmount.compareTo(discount.getMinPurchaseAmount()) < 0) {
            return null;
        }
        
        // Check usage limit
        if (discount.getUsageLimit() != null && 
            discount.getUsageCount() >= discount.getUsageLimit()) {
            return null;
        }
        
        return mapToResponse(discount);
    }
    
    private DiscountResponse mapToResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setCode(discount.getCode());
        response.setName(discount.getName());
        response.setDescription(discount.getDescription());
        response.setDiscountType(discount.getDiscountType());
        response.setDiscountValue(discount.getDiscountValue());
        response.setMinPurchaseAmount(discount.getMinPurchaseAmount());
        response.setMaxDiscountAmount(discount.getMaxDiscountAmount());
        response.setStartDate(discount.getStartDate());
        response.setEndDate(discount.getEndDate());
        response.setUsageLimit(discount.getUsageLimit());
        response.setUsageCount(discount.getUsageCount());
        response.setIsActive(discount.getIsActive());
        response.setApplicableToAllProducts(discount.getApplicableToAllProducts());
        response.setProductIds(discount.getProductIds());
        response.setCustomerIds(discount.getCustomerIds());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());
        return response;
    }
}
