package hoang.graduation.share.constant;

import java.util.HashMap;
import java.util.Map;

public class PaidCode {
    public static final Map<String, String> TRANSACTION_ERRORS = new HashMap<>();
    public static final Map<String, String> QUERY_ERRORS = new HashMap<>();
    public static final Map<String, String> REFUND_ERRORS = new HashMap<>();

    static {
        // Giao dịch thành công
        TRANSACTION_ERRORS.put("00", "Giao dịch thành công");
        TRANSACTION_ERRORS.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        TRANSACTION_ERRORS.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        TRANSACTION_ERRORS.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.");
        TRANSACTION_ERRORS.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        TRANSACTION_ERRORS.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        TRANSACTION_ERRORS.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
        TRANSACTION_ERRORS.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch.");
        TRANSACTION_ERRORS.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        TRANSACTION_ERRORS.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        TRANSACTION_ERRORS.put("75", "Ngân hàng thanh toán đang bảo trì.");
        TRANSACTION_ERRORS.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
        TRANSACTION_ERRORS.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê).");

        // Tra cứu giao dịch (vnp_Command=querydr)
        QUERY_ERRORS.put("02", "Merchant không hợp lệ (kiểm tra lại vnp_TmnCode).");
        QUERY_ERRORS.put("03", "Dữ liệu gửi sang không đúng định dạng.");
        QUERY_ERRORS.put("91", "Không tìm thấy giao dịch yêu cầu.");
        QUERY_ERRORS.put("94", "Yêu cầu bị trùng lặp trong thời gian giới hạn của API (Giới hạn trong 5 phút).");
        QUERY_ERRORS.put("97", "Chữ ký không hợp lệ.");
        QUERY_ERRORS.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê).");

        // Gửi yêu cầu hoàn trả (vnp_Command=refund)
        REFUND_ERRORS.put("02", "Tổng số tiền hoàn trả lớn hơn số tiền gốc.");
        REFUND_ERRORS.put("03", "Dữ liệu gửi sang không đúng định dạng.");
        REFUND_ERRORS.put("04", "Không cho phép hoàn trả toàn phần sau khi hoàn trả một phần.");
        REFUND_ERRORS.put("13", "Chỉ cho phép hoàn trả một phần.");
        REFUND_ERRORS.put("91", "Không tìm thấy giao dịch yêu cầu hoàn trả.");
        REFUND_ERRORS.put("93", "Số tiền hoàn trả không hợp lệ. Số tiền hoàn trả phải nhỏ hơn hoặc bằng số tiền thanh toán.");
        REFUND_ERRORS.put("94", "Yêu cầu bị trùng lặp trong thời gian giới hạn của API (Giới hạn trong 5 phút).");
        REFUND_ERRORS.put("95", "Giao dịch này không thành công bên VNPAY. VNPAY từ chối xử lý yêu cầu.");
        REFUND_ERRORS.put("97", "Chữ ký không hợp lệ.");
        REFUND_ERRORS.put("98", "Timeout Exception.");
        REFUND_ERRORS.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê).");
    }

    public static String getTransactionError(String code) {
        return TRANSACTION_ERRORS.getOrDefault(code, "Không tìm thấy mã lỗi");
    }

    public static String getQueryError(String code) {
        return QUERY_ERRORS.getOrDefault(code, "Không tìm thấy mã lỗi");
    }

    public static String getRefundError(String code) {
        return REFUND_ERRORS.getOrDefault(code, "Không tìm thấy mã lỗi");
    }
}
