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
    
    List<Price> findByProductIdAndPriceType(Long productId, String priceType);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId " +
           "AND (p.warehouseId = :warehouseId OR p.warehouseId IS NULL) " +
           "AND p.priceType = :priceType " +
           "AND p.isActive = true " +
           "AND (p.validTo IS NULL OR p.validTo > :now) " +
           "AND p.validFrom <= :now " +
           "ORDER BY p.validFrom DESC")
    Optional<Price> findCurrentPrice(@Param("productId") Long productId,
                                     @Param("warehouseId") Long warehouseId,
                                     @Param("priceType") String priceType,
                                     @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId " +
           "AND (p.warehouseId = :warehouseId OR p.warehouseId IS NULL) " +
           "ORDER BY p.validFrom DESC")
    List<Price> findPriceHistory(@Param("productId") Long productId,
                                 @Param("warehouseId") Long warehouseId);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId " +
           "AND p.warehouseId = :warehouseId " +
           "AND p.priceType = :priceType")
    Optional<Price> findByProductIdAndWarehouseIdAndPriceType(@Param("productId") Long productId,
                                                               @Param("warehouseId") Long warehouseId,
                                                               @Param("priceType") String priceType);
    
    @Query("SELECT p FROM Price p WHERE p.productId = :productId " +
           "AND p.warehouseId IS NULL " +
           "AND p.priceType = :priceType")
    Optional<Price> findGlobalPriceByProductIdAndPriceType(@Param("productId") Long productId,
                                                           @Param("priceType") String priceType);
}

