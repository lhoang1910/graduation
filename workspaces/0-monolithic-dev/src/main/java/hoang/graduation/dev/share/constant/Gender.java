package hoang.graduation.dev.share.constant;

import java.util.HashMap;
import java.util.Map;

public class Gender {
    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int OTHER = 2;

    public static Map<Integer, String> gender;
    static {
        gender = new HashMap<>();
        gender.put(MALE, "Nam");
        gender.put(FEMALE, "Nữ");
        gender.put(OTHER, "Khác");
    }
}
