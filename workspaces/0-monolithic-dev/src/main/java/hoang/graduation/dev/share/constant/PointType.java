package hoang.graduation.dev.share.constant;

import java.util.HashMap;
import java.util.Map;

public class PointType {
    public static final int FOLLOW_ANSWER = 0;
    public static final int FOLLOW_QUESTION = 1;

    public static Map<Integer, String> pointType;
    static {
        pointType = new HashMap<>();
        pointType.put(FOLLOW_ANSWER, "Tính theo tổng số đáp án");
        pointType.put(FOLLOW_QUESTION, "Tính theo tổng số câu hỏi");
    }
}
