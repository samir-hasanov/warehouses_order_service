package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Discount entity - discount rules and campaigns
 */
@Entity
@Table(name = "discounts", schema = "click_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Discount extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code; // Discount code/promo code

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue; // Percentage or fixed amount

    @Column(name = "min_purchase_amount", precision = 15, scale = 2)
    private BigDecimal minPurchaseAmount; // Minimum order amount to apply discount

    @Column(name = "max_discount_amount", precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount; // Maximum discount cap

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit; // Maximum number of times this discount can be used

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0; // Current usage count

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "applicable_to_all_products", nullable = false)
    private Boolean applicableToAllProducts = true;

    @Column(name = "product_ids", columnDefinition = "TEXT")
    private String productIds; // Comma-separated product IDs if not applicable to all

    @Column(name = "customer_ids", columnDefinition = "TEXT")
    private String customerIds; // Comma-separated customer IDs if restricted
}
