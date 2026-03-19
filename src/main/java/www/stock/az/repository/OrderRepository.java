package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Order;
import www.stock.az.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime fromDate, LocalDateTime toDate);

    List<Order> findByOrderStatusOrderByOrderDateDesc(OrderStatus status);

    List<Order> findByOrderStatusAndOrderDateBetweenOrderByOrderDateDesc(
            OrderStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );
}

