package hoang.graduation.dev.component;

import hoang.graduation.dev.module.classes.repo.ClassRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FinderUtils {
    private final ClassRepo classRepo;

    public List<String> getUserClassCodes(String userEmail){
        List<String> response = classRepo.findClassCodeByUserEmailIn(userEmail);
        return CollectionUtils.isEmpty(response) ? null : response;
    }
}
