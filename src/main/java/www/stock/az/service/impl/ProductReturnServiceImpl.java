package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.ProductReturnCreateRequest;
import www.stock.az.dto.response.ProductReturnResponse;
import www.stock.az.entity.ProductReturn;
import www.stock.az.enums.ReturnStatus;
import www.stock.az.repository.ProductReturnRepository;
import www.stock.az.service.ProductReturnService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReturnServiceImpl implements ProductReturnService {

    private final ProductReturnRepository productReturnRepository;

    @Override
    public ProductReturnResponse create(ProductReturnCreateRequest request) {
        ProductReturn r = new ProductReturn();
        r.setReturnNumber(request.getReturnNumber());
        r.setOrderId(request.getOrderId());
        r.setInvoiceId(request.getInvoiceId());
        r.setReturnDate(request.getReturnDate() != null ? request.getReturnDate() : LocalDateTime.now());
        r.setStatus(ReturnStatus.PENDING);
        r.setTotalAmount(request.getTotalAmount());
        r.setNotes(request.getNotes());
        r.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        ProductReturn saved = productReturnRepository.save(r);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReturnResponse findById(Long id) {
        ProductReturn r = productReturnRepository.findById(id).orElseThrow(() -> new RuntimeException("Geri qaytarma tapılmadı: " + id));
        return mapToResponse(r);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReturnResponse findByReturnNumber(String returnNumber) {
        ProductReturn r = productReturnRepository.findByReturnNumber(returnNumber).orElseThrow(() -> new RuntimeException("Geri qaytarma tapılmadı: " + returnNumber));
        return mapToResponse(r);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReturnResponse> search(ReturnStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        return productReturnRepository.search(status, fromDate, toDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductReturnResponse mapToResponse(ProductReturn r) {
        ProductReturnResponse res = new ProductReturnResponse();
        res.setId(r.getId());
        res.setReturnNumber(r.getReturnNumber());
        res.setOrderId(r.getOrderId());
        res.setInvoiceId(r.getInvoiceId());
        res.setReturnDate(r.getReturnDate());
        res.setStatus(r.getStatus());
        res.setTotalAmount(r.getTotalAmount());
        res.setNotes(r.getNotes());
        res.setCurrency(r.getCurrency());
        return res;
    }
}
