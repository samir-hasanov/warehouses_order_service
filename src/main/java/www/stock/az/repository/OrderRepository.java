package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Order;
import www.stock.az.enums.OrderStatus;
import www.stock.az.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByOrderStatus(OrderStatus status);
    
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<Order> findByOrderStatusAndPaymentStatus(OrderStatus orderStatus, PaymentStatus paymentStatus);
    
    List<Order> findByCustomerEmail(String customerEmail);
    
    List<Order> findByCustomerPhone(String customerPhone);
    
    List<Order> findByWarehouseId(Long warehouseId);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status AND o.orderDate >= :startDate")
    List<Order> findByStatusAndDateAfter(@Param("status") OrderStatus status, 
                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByOrderStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:query% OR o.customerName LIKE %:query% OR o.customerEmail LIKE %:query%")
    List<Order> searchOrders(@Param("query") String query);
}
