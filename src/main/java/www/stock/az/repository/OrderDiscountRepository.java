package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.OrderDiscount;

import java.util.List;

@Repository
public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Long> {
    
    List<OrderDiscount> findByOrderId(Long orderId);
    
    List<OrderDiscount> findByDiscountId(Long discountId);
}
