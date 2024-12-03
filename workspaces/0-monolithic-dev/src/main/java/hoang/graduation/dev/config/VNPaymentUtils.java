package hoang.graduation.dev.config;

import hoang.graduation.dev.module.payment.entity.PaymentHistoryEntity;
import hoang.graduation.dev.module.payment.repo.PaymentHistoryRepo;
import hoang.graduation.dev.share.constant.PaidCode;
import hoang.graduation.dev.share.model.request.payment.CreatePaymentHistoryRequest;
import hoang.graduation.dev.share.utils.DateTimesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class VNPaymentUtils {

    private final PaymentHistoryRepo paymentHistoryRepo;

    public String createOrder(Long orderTotal, String orderInfor, String urlReturn) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(orderTotal * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VNPayConfig.vnp_Returnurl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public int orderReturn(CreatePaymentHistoryRequest paymentRequest, HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
            fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayConfig.hashAllFields(fields);
        int returnValue;
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                returnValue = 1;
            } else {
                returnValue = 0;
            }
        } else {
            returnValue = -1;
        }
        paymentHistoryRepo.save(PaymentHistoryEntity.builder()
                .id(UUID.randomUUID().toString())
//                .bankCodeNumber()         // khong co thong tin
                .bankFullName(request.getParameter("vnp_Amount"))
                .content(request.getParameter("vnp_OrderInfo"))
                .bankTranNo(request.getParameter("vnp_OrderInfo"))
                .paymentStatus(PaidCode.getTransactionError(request.getParameter("vnp_TransactionStatus")))
                .bankName(request.getParameter("vnp_BankCode"))
                .cardType(request.getParameter("vnp_CardType"))
                .paymentAmount(paymentRequest.getPaymentAmount())
                .paymentDate(DateTimesUtils.convertStringToDate(request.getParameter("vnp_PayDate"), "yyyy-MM-dd'T'HH:mm:ss.SSSSX"))
                .paymentBy(paymentRequest.getPaymentBy())
                .description(paymentRequest.getDescription())
                .hasInvoiced(false)
                .serviceName(paymentRequest.getServiceName())
                .paymentStatus(request.getParameter("vnp_TransactionStatus"))
                .build());

        return returnValue;
    }
}
