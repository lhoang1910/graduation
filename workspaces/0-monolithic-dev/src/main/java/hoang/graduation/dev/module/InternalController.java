package hoang.graduation.dev.module;

import hoang.graduation.dev.module.user.query.QueryDetail;
import hoang.graduation.dev.share.model.response.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api")
@RequiredArgsConstructor
public class InternalController {
    private final QueryDetail queryDetail;

    @GetMapping("/profile")
    public WrapResponse<?> getDetail() {
        return queryDetail.getDetail();
    }
}
