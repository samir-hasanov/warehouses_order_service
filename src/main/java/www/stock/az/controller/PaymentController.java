package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.PaymentCreateRequest;
import www.stock.az.dto.response.PaymentResponse;
import www.stock.az.enums.PaymentType;
import www.stock.az.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/1.1/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> search(
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        PaymentType pt = null;
        if (paymentType != null && !paymentType.isBlank()) {
            try {
                pt = PaymentType.valueOf(paymentType.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(paymentService.search(pt, fromDate, toDate));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.findByOrderId(orderId));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponse>> findByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.findByInvoiceId(invoiceId));
    }
}
