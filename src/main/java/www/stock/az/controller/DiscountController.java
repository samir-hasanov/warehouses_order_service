package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.DiscountCreateRequest;
import www.stock.az.dto.request.DiscountUpdateRequest;
import www.stock.az.dto.response.DiscountResponse;
import www.stock.az.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/discounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountResponse> create(@Valid @RequestBody DiscountCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponse> update(@PathVariable Long id, @Valid @RequestBody DiscountUpdateRequest request) {
        return ResponseEntity.ok(discountService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.findById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DiscountResponse> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(discountService.findByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponse>> getAll() {
        return ResponseEntity.ok(discountService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponse>> getActive() {
        return ResponseEntity.ok(discountService.getActive());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        discountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
