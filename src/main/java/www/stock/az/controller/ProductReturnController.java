package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.ProductReturnCreateRequest;
import www.stock.az.dto.response.ProductReturnResponse;
import www.stock.az.enums.ReturnStatus;
import www.stock.az.service.ProductReturnService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/1.1/returns")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductReturnController {

    private final ProductReturnService productReturnService;

    @PostMapping
    public ResponseEntity<ProductReturnResponse> create(@Valid @RequestBody ProductReturnCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productReturnService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductReturnResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productReturnService.findById(id));
    }

    @GetMapping("/number/{returnNumber}")
    public ResponseEntity<ProductReturnResponse> findByReturnNumber(@PathVariable String returnNumber) {
        return ResponseEntity.ok(productReturnService.findByReturnNumber(returnNumber));
    }

    @GetMapping
    public ResponseEntity<List<ProductReturnResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        ReturnStatus st = null;
        if (status != null && !status.isBlank()) {
            try {
                st = ReturnStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(productReturnService.search(st, fromDate, toDate));
    }
}
