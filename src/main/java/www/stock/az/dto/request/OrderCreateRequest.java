package www.stock.az.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import www.stock.az.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateRequest {

    @NotBlank
    private String orderNumber;

    @NotNull
    private LocalDateTime orderDate;

    private String customerName;

    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private Long warehouseId;
    private String currency;
    private String notes;

    private OrderStatus orderStatus;

    @Valid
    @NotNull
    @JsonAlias("orderItems")
    private List<OrderItemRequest> items;
}

