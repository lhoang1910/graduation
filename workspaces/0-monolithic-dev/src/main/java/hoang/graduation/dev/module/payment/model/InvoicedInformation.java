package hoang.graduation.dev.module.payment.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InvoicedInformation {
    private String invoiceNumber;
    private String createdDate;
    private String createdBy;
    private String requestBy;
}
