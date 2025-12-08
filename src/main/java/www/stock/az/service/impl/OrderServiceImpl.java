package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.OrderCreateRequest;
import www.stock.az.dto.request.OrderUpdateRequest;
import www.stock.az.dto.response.OrderDiscountResponse;
import www.stock.az.dto.response.OrderItemResponse;
import www.stock.az.dto.response.OrderResponse;
import www.stock.az.entity.Order;
import www.stock.az.entity.OrderDiscount;
import www.stock.az.entity.OrderItem;
import www.stock.az.enums.OrderStatus;
import www.stock.az.enums.PaymentStatus;
import www.stock.az.repository.OrderRepository;
import www.stock.az.service.DiscountService;
import www.stock.az.service.OrderService;
import www.stock.az.service.PriceService;
import www.stock.az.dto.response.PriceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final DiscountService discountService;
    private final PriceService priceService;
    
    @Override
    public OrderResponse create(OrderCreateRequest request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setWarehouseId(request.getWarehouseId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        order.setNotes(request.getNotes());
        
        // Calculate order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var itemRequest : request.getOrderItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            
            // Get current price
            PriceResponse currentPrice = priceService.getCurrentPrice(
                itemRequest.getProductId(), 
                request.getWarehouseId(), 
                "SELLING"
            );
            
            if (currentPrice == null) {
                throw new RuntimeException("Price not found for product: " + itemRequest.getProductId());
            }
            
            item.setUnitPrice(currentPrice.getUnitPrice());
            item.setSubtotal(item.getQuantity().multiply(item.getUnitPrice()));
            
            // Apply item-level discount if any
            if (itemRequest.getDiscountPercentage() != null) {
                item.setDiscountPercentage(itemRequest.getDiscountPercentage());
                item.setDiscountAmount(item.getSubtotal()
                    .multiply(itemRequest.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100)));
            } else {
                item.setDiscountAmount(BigDecimal.ZERO);
            }
            
            item.setTotal(item.getSubtotal().subtract(item.getDiscountAmount()));
            item.setTaxAmount(BigDecimal.ZERO); // Can be calculated based on tax rules
            item.setNotes(itemRequest.getNotes());
            
            order.getOrderItems().add(item);
            subtotal = subtotal.add(item.getTotal());
        }
        
        order.setSubtotal(subtotal);
        
        // Apply discounts
        BigDecimal totalDiscount = BigDecimal.ZERO;
        if (request.getDiscountCodes() != null && !request.getDiscountCodes().isEmpty()) {
            for (String code : request.getDiscountCodes()) {
                var discount = discountService.validate(code, subtotal);
                if (discount != null) {
                    // Apply discount logic
                    // This is simplified - actual implementation would be more complex
                    totalDiscount = totalDiscount.add(calculateDiscount(discount, subtotal));
                }
            }
        }
        
        order.setTotalDiscount(totalDiscount);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setShippingCost(BigDecimal.ZERO);
        order.setTotalAmount(subtotal.subtract(totalDiscount).add(order.getTaxAmount()).add(order.getShippingCost()));
        
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return mapToResponse(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByPaymentStatus(PaymentStatus paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> search(String query) {
        return orderRepository.searchOrders(query).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public OrderResponse update(Long id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }
        if (request.getCustomerName() != null) {
            order.setCustomerName(request.getCustomerName());
        }
        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }
        if (request.getCustomerPhone() != null) {
            order.setCustomerPhone(request.getCustomerPhone());
        }
        if (request.getDeliveryAddress() != null) {
            order.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }
    
    @Override
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        order.setOrderStatus(status);
        
        if (status == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }
    
    @Override
    public OrderResponse updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        order.setPaymentStatus(paymentStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }
    
    @Override
    public void cancel(Long id, String reason) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        if (order.getOrderStatus() == OrderStatus.COMPLETED || 
            order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getOrderStatus());
        }
        
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setNotes(order.getNotes() != null ? 
            order.getNotes() + "\nCancelled: " + reason : 
            "Cancelled: " + reason);
        
        orderRepository.save(order);
    }
    
    @Override
    public void complete(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    private BigDecimal calculateDiscount(www.stock.az.dto.response.DiscountResponse discount, BigDecimal amount) {
        // Simplified discount calculation
        // Actual implementation would be more complex
        return BigDecimal.ZERO;
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
        
        response.setOrderItems(order.getOrderItems().stream()
            .map(this::mapItemToResponse)
            .collect(Collectors.toList()));
        
        response.setOrderDiscounts(order.getOrderDiscounts().stream()
            .map(this::mapDiscountToResponse)
            .collect(Collectors.toList()));
        
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return response;
    }
    
    private OrderItemResponse mapItemToResponse(OrderItem item) {
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
    
    private OrderDiscountResponse mapDiscountToResponse(OrderDiscount discount) {
        OrderDiscountResponse response = new OrderDiscountResponse();
        response.setId(discount.getId());
        response.setDiscountId(discount.getDiscount() != null ? discount.getDiscount().getId() : null);
        response.setDiscountType(discount.getDiscountType());
        response.setDiscountName(discount.getDiscountName());
        response.setDiscountValue(discount.getDiscountValue());
        response.setDiscountAmount(discount.getDiscountAmount());
        response.setPromoCode(discount.getPromoCode());
        response.setNotes(discount.getNotes());
        return response;
    }
}
