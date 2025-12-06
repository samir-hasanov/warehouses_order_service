package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Price entity - manages product pricing (stock prices controlled by order service)
 */
@Entity
@Table(name = "prices", schema = "click_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id", "price_type"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Price extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private Long productId; // Reference to product in stock service

    @Column(name = "warehouse_id")
    private Long warehouseId; // null means price applies to all warehouses

    @Column(name = "price_type", nullable = false, length = 50)
    private String priceType; // SELLING, COST, WHOLESALE, RETAIL, etc.

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "currency", length = 3)
    private String currency = "AZN"; // Default currency

    @Column(name = "min_quantity", precision = 15, scale = 3)
    private BigDecimal minQuantity; // Minimum quantity for this price tier

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo; // null means no expiration

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "notes", length = 500)
    private String notes;

    @Version
    @Column(name = "version")
    private Long version; // Optimistic locking for concurrent updates
}
