package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.PaymentCreateRequest;
import www.stock.az.dto.response.PaymentResponse;
import www.stock.az.entity.Payment;
import www.stock.az.enums.PaymentType;
import www.stock.az.repository.PaymentRepository;
import www.stock.az.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse create(PaymentCreateRequest request) {
        Payment p = new Payment();
        p.setDocumentNumber(request.getDocumentNumber());
        p.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());
        p.setDescription(request.getDescription());
        p.setDebit(request.getDebit());
        p.setCredit(request.getCredit());
        p.setAmount(request.getAmount());
        p.setPurpose(request.getPurpose());
        p.setPaymentType(request.getPaymentType());
        p.setCounterpartyAccount(request.getCounterpartyAccount());
        p.setCounterpartyVoen(request.getCounterpartyVoen());
        p.setCounterpartyName(request.getCounterpartyName());
        p.setRunningBalance(request.getRunningBalance());
        p.setOrderId(request.getOrderId());
        p.setInvoiceId(request.getInvoiceId());
        p.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        Payment saved = paymentRepository.save(p);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Payment p = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Ödəniş tapılmadı: " + id));
        return mapToResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> search(PaymentType paymentType, LocalDateTime fromDate, LocalDateTime toDate) {
        return paymentRepository.search(paymentType, fromDate, toDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderIdOrderByPaymentDateDesc(orderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setDocumentNumber(p.getDocumentNumber());
        r.setPaymentDate(p.getPaymentDate());
        r.setDescription(p.getDescription());
        r.setDebit(p.getDebit());
        r.setCredit(p.getCredit());
        r.setAmount(p.getAmount());
        r.setPurpose(p.getPurpose());
        r.setPaymentType(p.getPaymentType());
        r.setCounterpartyAccount(p.getCounterpartyAccount());
        r.setCounterpartyVoen(p.getCounterpartyVoen());
        r.setCounterpartyName(p.getCounterpartyName());
        r.setRunningBalance(p.getRunningBalance());
        r.setOrderId(p.getOrderId());
        r.setInvoiceId(p.getInvoiceId());
        r.setCurrency(p.getCurrency());
        return r;
    }
}
