package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.DiscountType;

import java.math.BigDecimal;

/**
 * OrderDiscount entity - discounts applied to orders
 */
@Entity
@Table(name = "order_discounts", schema = "click_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDiscount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount; // Reference to discount rule

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType;

    @Column(name = "discount_name", length = 200)
    private String discountName; // Name/description of the discount

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue; // Amount or percentage

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount; // Calculated discount amount

    @Column(name = "promo_code", length = 50)
    private String promoCode; // Promotional code used

    @Column(name = "notes", length = 500)
    private String notes;
}
