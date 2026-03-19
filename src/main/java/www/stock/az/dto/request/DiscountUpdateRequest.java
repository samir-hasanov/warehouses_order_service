package www.stock.az.dto.request;

import lombok.Data;
import www.stock.az.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountUpdateRequest {

    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Boolean isActive;
    private Boolean applicableToAllProducts;
    private String productIds;
    private String customerIds;
}
