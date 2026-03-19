package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Payment;
import www.stock.az.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderIdOrderByPaymentDateDesc(Long orderId);

    List<Payment> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId);

    @Query("select p from Payment p where " +
            "(:paymentType is null or p.paymentType = :paymentType) " +
            "and (:fromDate is null or p.paymentDate >= :fromDate) " +
            "and (:toDate is null or p.paymentDate <= :toDate) " +
            "order by p.paymentDate desc")
    List<Payment> search(
            @Param("paymentType") PaymentType paymentType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
