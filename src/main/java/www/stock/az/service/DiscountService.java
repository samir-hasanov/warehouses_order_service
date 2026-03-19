package www.stock.az.service;

import www.stock.az.dto.request.DiscountCreateRequest;
import www.stock.az.dto.request.DiscountUpdateRequest;
import www.stock.az.dto.response.DiscountResponse;

import java.util.List;

public interface DiscountService {

    DiscountResponse create(DiscountCreateRequest request);

    DiscountResponse update(Long id, DiscountUpdateRequest request);

    DiscountResponse findById(Long id);

    DiscountResponse findByCode(String code);

    List<DiscountResponse> getAll();

    List<DiscountResponse> getActive();

    void deleteById(Long id);
}
