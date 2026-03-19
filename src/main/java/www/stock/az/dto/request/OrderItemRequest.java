package www.stock.az.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    @NotNull
    private Long productId;

    @NotBlank
    private String productCode;

    @NotBlank
    private String productName;

    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal unitPrice;

    private BigDecimal discountAmount;
    private BigDecimal discountPercentage;
    private BigDecimal taxAmount;
    private String notes;
}

