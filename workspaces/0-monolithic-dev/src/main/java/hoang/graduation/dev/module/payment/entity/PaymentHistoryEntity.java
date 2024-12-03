package hoang.graduation.dev.module.payment.entity;

import hoang.graduation.dev.module.payment.model.InvoicedInformation;
import hoang.graduation.dev.share.constant.JpaType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "payment_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentHistoryEntity {
    @Id
    private String id;

    private String serviceName;
    private Double paymentAmount;
    private String description;

    private String cardType;
    private String bankFullName;       // Ten chu tai khoan
    private String bankTranNo;
    private String bankCodeNumber;
    private String bankName;
    private String content;
    private Date paymentDate;
    private boolean hasInvoiced;
    private String paymentBy;       // email
    private String paymentStatus;

    @Column(columnDefinition = JpaType.JSON_BINARY)
    @Type(type = JpaType.JSON_BINARY)
    private InvoicedInformation invoicedInformation;
}
