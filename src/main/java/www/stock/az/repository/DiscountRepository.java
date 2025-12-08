package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Discount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    
    Optional<Discount> findByCode(String code);
    
    List<Discount> findByIsActiveTrue();
    
    @Query("SELECT d FROM Discount d WHERE d.isActive = true AND d.startDate <= :now AND (d.endDate IS NULL OR d.endDate >= :now)")
    List<Discount> findActiveDiscounts(@Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM Discount d WHERE d.code = :code AND d.isActive = true AND d.startDate <= :now AND (d.endDate IS NULL OR d.endDate >= :now)")
    Optional<Discount> findActiveDiscountByCode(@Param("code") String code, @Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM Discount d WHERE d.name LIKE %:query% OR d.code LIKE %:query%")
    List<Discount> searchDiscounts(@Param("query") String query);
}
