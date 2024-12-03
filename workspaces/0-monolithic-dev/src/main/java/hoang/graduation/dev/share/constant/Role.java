package hoang.graduation.dev.share.constant;

import java.util.HashMap;
import java.util.Map;

public class Role {
    public static final int TEACHER = 0;
    public static final int STUDENT = 1;
    public static final int ADMIN = 2;
    public static final int NORMAL = 3;

    public static Map<Integer, String> gender;
    static {
        gender = new HashMap<>();
        gender.put(TEACHER, "Giáo viên");
        gender.put(STUDENT, "Học sinh");
        gender.put(ADMIN, "Admin");
        gender.put(ADMIN, "Người dùng thường");
    }
}
