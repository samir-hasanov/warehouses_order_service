package www.stock.az.service;

import www.stock.az.dto.request.ProductReturnCreateRequest;
import www.stock.az.dto.response.ProductReturnResponse;
import www.stock.az.enums.ReturnStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductReturnService {

    ProductReturnResponse create(ProductReturnCreateRequest request);

    ProductReturnResponse findById(Long id);

    ProductReturnResponse findByReturnNumber(String returnNumber);

    List<ProductReturnResponse> search(ReturnStatus status, LocalDateTime fromDate, LocalDateTime toDate);
}
