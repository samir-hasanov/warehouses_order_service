package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.DiscountCreateRequest;
import www.stock.az.dto.request.DiscountUpdateRequest;
import www.stock.az.dto.response.DiscountResponse;
import www.stock.az.entity.Discount;
import www.stock.az.repository.DiscountRepository;
import www.stock.az.service.DiscountService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public DiscountResponse create(DiscountCreateRequest request) {
        Discount d = new Discount();
        d.setCode(request.getCode());
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        d.setDiscountType(request.getDiscountType());
        d.setDiscountValue(request.getDiscountValue());
        d.setMinPurchaseAmount(request.getMinPurchaseAmount());
        d.setMaxDiscountAmount(request.getMaxDiscountAmount());
        d.setStartDate(request.getStartDate());
        d.setEndDate(request.getEndDate());
        d.setUsageLimit(request.getUsageLimit());
        d.setUsageCount(0);
        d.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        d.setApplicableToAllProducts(request.getApplicableToAllProducts() != null ? request.getApplicableToAllProducts() : true);
        d.setProductIds(request.getProductIds());
        d.setCustomerIds(request.getCustomerIds());
        Discount saved = discountRepository.save(d);
        return mapToResponse(saved);
    }

    @Override
    public DiscountResponse update(Long id, DiscountUpdateRequest request) {
        Discount d = discountRepository.findById(id).orElseThrow(() -> new RuntimeException("Endirim tapılmadı: " + id));
        if (request.getCode() != null) d.setCode(request.getCode());
        if (request.getName() != null) d.setName(request.getName());
        if (request.getDescription() != null) d.setDescription(request.getDescription());
        if (request.getDiscountType() != null) d.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) d.setDiscountValue(request.getDiscountValue());
        if (request.getMinPurchaseAmount() != null) d.setMinPurchaseAmount(request.getMinPurchaseAmount());
        if (request.getMaxDiscountAmount() != null) d.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getStartDate() != null) d.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) d.setEndDate(request.getEndDate());
        if (request.getUsageLimit() != null) d.setUsageLimit(request.getUsageLimit());
        if (request.getIsActive() != null) d.setIsActive(request.getIsActive());
        if (request.getApplicableToAllProducts() != null) d.setApplicableToAllProducts(request.getApplicableToAllProducts());
        if (request.getProductIds() != null) d.setProductIds(request.getProductIds());
        if (request.getCustomerIds() != null) d.setCustomerIds(request.getCustomerIds());
        return mapToResponse(discountRepository.save(d));
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse findById(Long id) {
        Discount d = discountRepository.findById(id).orElseThrow(() -> new RuntimeException("Endirim tapılmadı: " + id));
        return mapToResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse findByCode(String code) {
        Discount d = discountRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Endirim tapılmadı: " + code));
        return mapToResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAll() {
        return discountRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActive() {
        return discountRepository.findByIsActiveTrue().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        discountRepository.deleteById(id);
    }

    private DiscountResponse mapToResponse(Discount d) {
        DiscountResponse r = new DiscountResponse();
        r.setId(d.getId());
        r.setCode(d.getCode());
        r.setName(d.getName());
        r.setDescription(d.getDescription());
        r.setDiscountType(d.getDiscountType());
        r.setDiscountValue(d.getDiscountValue());
        r.setMinPurchaseAmount(d.getMinPurchaseAmount());
        r.setMaxDiscountAmount(d.getMaxDiscountAmount());
        r.setStartDate(d.getStartDate());
        r.setEndDate(d.getEndDate());
        r.setUsageLimit(d.getUsageLimit());
        r.setUsageCount(d.getUsageCount());
        r.setIsActive(d.getIsActive());
        r.setApplicableToAllProducts(d.getApplicableToAllProducts());
        r.setProductIds(d.getProductIds());
        r.setCustomerIds(d.getCustomerIds());
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());
        return r;
    }
}
