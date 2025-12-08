package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    
    List<Price> findByProductId(Long productId);
    
    List<Price> findByWarehouseId(Long warehouseId);
    
    List<Price> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId AND p.isActive = true AND " +
           "p.validFrom <= :now AND (p.validTo IS NULL OR p.validTo >= :now) " +
           "ORDER BY p.validFrom DESC")
    List<Price> findActivePricesByProduct(@Param("productId") Long productId, @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId AND p.warehouseId = :warehouseId AND " +
           "p.priceType = :priceType AND p.isActive = true AND " +
           "p.validFrom <= :now AND (p.validTo IS NULL OR p.validTo >= :now) " +
           "ORDER BY p.validFrom DESC")
    Optional<Price> findCurrentPrice(@Param("productId") Long productId, 
                                     @Param("warehouseId") Long warehouseId,
                                     @Param("priceType") String priceType,
                                     @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId AND " +
           "(p.warehouseId = :warehouseId OR p.warehouseId IS NULL) AND " +
           "p.priceType = :priceType AND p.isActive = true AND " +
           "p.validFrom <= :now AND (p.validTo IS NULL OR p.validTo >= :now) " +
           "ORDER BY p.warehouseId NULLS LAST, p.validFrom DESC")
    List<Price> findCurrentPrices(@Param("productId") Long productId,
                                  @Param("warehouseId") Long warehouseId,
                                  @Param("priceType") String priceType,
                                  @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId AND " +
           "(:warehouseId IS NULL OR p.warehouseId = :warehouseId OR p.warehouseId IS NULL) AND " +
           "p.validFrom <= :endDate AND (p.validTo IS NULL OR p.validTo >= :startDate) " +
           "ORDER BY p.validFrom DESC")
    List<Price> findPriceHistory(@Param("productId") Long productId,
                                 @Param("warehouseId") Long warehouseId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
}
