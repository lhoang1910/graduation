package hoang.graduation.dev.share.model.request.payment;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreatePaymentHistoryRequest {
    private String serviceName;
    private Double paymentAmount;
    private String description;

    private String cardType;
    private String bankFullName;       // Ten chu tai khoan
    private String bankCodeNumber;
    private String bankName;
    private String content;
    private Date paymentDate;
    private String hasInvoiced;
    private String paymentBy;       // email
}
