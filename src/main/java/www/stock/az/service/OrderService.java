package www.stock.az.service;

import www.stock.az.dto.request.OrderCreateRequest;
import www.stock.az.dto.response.OrderResponse;
import www.stock.az.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderResponse create(OrderCreateRequest request);

    OrderResponse findById(Long id);

    OrderResponse findByNumber(String orderNumber);

    List<OrderResponse> search(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate);
}

