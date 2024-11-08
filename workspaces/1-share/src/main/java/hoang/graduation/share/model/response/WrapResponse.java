package hoang.graduation.share.model.response;


import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WrapResponse<T> {
    private boolean isSuccess;
    private HttpStatus status;
    private String message;
    private T data;

    public WrapResponse(boolean isSuccess, HttpStatus status, String message) {
        this.isSuccess = isSuccess;
        this.status = status;
        this.message = message;
    }

    public WrapResponse ok(String message, T data){
        return WrapResponse.builder()
                .isSuccess(true)
                .status(HttpStatus.OK)
                .data(data)
                .message(message)
                .build();
    }
}
