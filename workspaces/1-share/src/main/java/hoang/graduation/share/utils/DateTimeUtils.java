package hoang.graduation.share.utils;

import java.util.Date;

public class DateTimeUtils {
    public static double calculateMinutesDifference(Date date1, Date date2) {
        long diffInMillis = Math.abs(date2.getTime() - date1.getTime());
        return diffInMillis / (1000.0 * 60);
    }
}
