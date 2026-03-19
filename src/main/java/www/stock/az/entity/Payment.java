package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ödəniş - Excel bank extract: Sənəd No, Tarix, Açıqlama, Debet, Kredit, Məbləğ, Təyinat.
 * Ödəniş növü: kart, nağd, əvəzləşmə.
 */
@Entity
@Table(name = "payments", schema = "order_service")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "document_number", length = 100)
    private String documentNumber;   // Sənəd Nömrəsi

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;  // Tarix

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;      // Açıqlama

    @Column(name = "debit", precision = 18, scale = 2)
    private BigDecimal debit;       // Debet

    @Column(name = "credit", precision = 18, scale = 2)
    private BigDecimal credit;      // Kredit

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;      // Məbləğ

    @Column(name = "purpose", length = 500)
    private String purpose;          // Təyinat

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20)
    private PaymentType paymentType; // Kart / Nağd / Əvəzləşmə

    @Column(name = "counterparty_account", length = 100)
    private String counterpartyAccount;  // Alıcının hesabı

    @Column(name = "counterparty_voen", length = 50)
    private String counterpartyVoen;

    @Column(name = "counterparty_name", length = 500)
    private String counterpartyName;    // Adı

    @Column(name = "running_balance", precision = 18, scale = 2)
    private BigDecimal runningBalance;   // Ara qalıq

    @Column(name = "order_id")
    private Long orderId;            // Sifarişə bağlı ola bilər

    @Column(name = "invoice_id")
    private Long invoiceId;          // Qaiməyə bağlı ola bilər

    @Column(name = "currency", length = 10)
    private String currency = "AZN";
}
