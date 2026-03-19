package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.ProductReturn;
import www.stock.az.enums.ReturnStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReturnRepository extends JpaRepository<ProductReturn, Long> {

    Optional<ProductReturn> findByReturnNumber(String returnNumber);

    List<ProductReturn> findByOrderIdOrderByReturnDateDesc(Long orderId);

    List<ProductReturn> findByInvoiceIdOrderByReturnDateDesc(Long invoiceId);

    @Query("select r from ProductReturn r where " +
            "(:status is null or r.status = :status) " +
            "and (:fromDate is null or r.returnDate >= :fromDate) " +
            "and (:toDate is null or r.returnDate <= :toDate) " +
            "order by r.returnDate desc")
    List<ProductReturn> search(
            @Param("status") ReturnStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
