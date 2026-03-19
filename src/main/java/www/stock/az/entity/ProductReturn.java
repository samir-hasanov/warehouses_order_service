package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.ReturnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Geri qaytarma - Excel ƏDV DH və istifadəçi tələbi ilə uyğun.
 */
@Entity
@Table(name = "product_returns", schema = "order_service")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductReturn extends BaseEntity {

    @Column(name = "return_number", nullable = false, unique = true, length = 100)
    private String returnNumber;     // Geri qaytarma nömrəsi

    @Column(name = "order_id")
    private Long orderId;            // Sifarişə aid ola bilər

    @Column(name = "invoice_id")
    private Long invoiceId;          // Qaiməyə aid ola bilər

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReturnStatus status = ReturnStatus.PENDING;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "currency", length = 10)
    private String currency = "AZN";
}
