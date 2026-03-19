package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.OrderCreateRequest;
import www.stock.az.dto.request.OrderItemRequest;
import www.stock.az.dto.response.OrderItemResponse;
import www.stock.az.dto.response.OrderResponse;
import www.stock.az.entity.Order;
import www.stock.az.entity.OrderItem;
import www.stock.az.enums.OrderStatus;
import www.stock.az.enums.PaymentStatus;
import www.stock.az.exception.ResourceNotFoundException;
import www.stock.az.repository.OrderRepository;
import www.stock.az.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse create(OrderCreateRequest request) {
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setOrderDate(request.getOrderDate());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setWarehouseId(request.getWarehouseId());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        order.setNotes(request.getNotes());
        order.setOrderStatus(request.getOrderStatus() != null ? request.getOrderStatus() : OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.getProductId());
            item.setProductCode(itemRequest.getProductCode());
            item.setProductName(itemRequest.getProductName());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());

            BigDecimal lineSubtotal = itemRequest.getUnitPrice().multiply(itemRequest.getQuantity());
            BigDecimal discount = itemRequest.getDiscountAmount() != null ? itemRequest.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal tax = itemRequest.getTaxAmount() != null ? itemRequest.getTaxAmount() : BigDecimal.ZERO;

            item.setDiscountAmount(discount);
            item.setDiscountPercentage(itemRequest.getDiscountPercentage());
            item.setSubtotal(lineSubtotal);
            item.setTotal(lineSubtotal.subtract(discount));
            item.setTaxAmount(tax);
            item.setNotes(itemRequest.getNotes());

            subtotal = subtotal.add(lineSubtotal);
            totalDiscount = totalDiscount.add(discount);
            taxAmount = taxAmount.add(tax);

            items.add(item);
        }

        order.setSubtotal(subtotal);
        order.setTotalDiscount(totalDiscount);
        order.setTaxAmount(taxAmount);
        order.setShippingCost(order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO);
        order.setTotalAmount(subtotal.subtract(totalDiscount).add(taxAmount).add(order.getShippingCost()));
        order.setOrderItems(items);

        Order saved = orderRepository.save(order);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sifariş tapılmadı: " + id));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sifariş tapılmadı: " + orderNumber));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> search(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        final List<Order> orders;
        if (fromDate != null || toDate != null) {
            // Optional tarix filter-lərində DB tip infer problemi olmaması üçün null göndərmirik.
            LocalDateTime effectiveFrom = fromDate != null ? fromDate : LocalDateTime.of(1970, 1, 1, 0, 0);
            LocalDateTime effectiveTo = toDate != null ? toDate : LocalDateTime.of(9999, 12, 31, 23, 59, 59);
            if (status != null) {
                orders = orderRepository.findByOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
                        status,
                        effectiveFrom,
                        effectiveTo
                );
            } else {
                orders = orderRepository.findByOrderDateBetweenOrderByOrderDateDesc(effectiveFrom, effectiveTo);
            }
        } else if (status != null) {
            orders = orderRepository.findByOrderStatusOrderByOrderDateDesc(status);
        } else {
            orders = orderRepository.findAllByOrderByOrderDateDesc();
        }

        return orders
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setOrderStatus(order.getOrderStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderDate(order.getOrderDate());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setSubtotal(order.getSubtotal());
        response.setTotalDiscount(order.getTotalDiscount());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingCost(order.getShippingCost());
        response.setTotalAmount(order.getTotalAmount());
        response.setCurrency(order.getCurrency());
        response.setNotes(order.getNotes());
        response.setWarehouseId(order.getWarehouseId());
        response.setCompletedAt(order.getCompletedAt());

        if (order.getOrderItems() != null) {
            response.setItems(order.getOrderItems().stream()
                    .map(this::mapItem)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private OrderItemResponse mapItem(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductCode(item.getProductCode());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setDiscountAmount(item.getDiscountAmount());
        response.setDiscountPercentage(item.getDiscountPercentage());
        response.setSubtotal(item.getSubtotal());
        response.setTotal(item.getTotal());
        response.setTaxAmount(item.getTaxAmount());
        response.setNotes(item.getNotes());
        return response;
    }
}

