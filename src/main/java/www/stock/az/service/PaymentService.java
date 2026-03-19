package www.stock.az.service;

import www.stock.az.dto.request.PaymentCreateRequest;
import www.stock.az.dto.response.PaymentResponse;
import www.stock.az.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponse create(PaymentCreateRequest request);

    PaymentResponse findById(Long id);

    List<PaymentResponse> search(PaymentType paymentType, LocalDateTime fromDate, LocalDateTime toDate);

    List<PaymentResponse> findByOrderId(Long orderId);

    List<PaymentResponse> findByInvoiceId(Long invoiceId);
}
