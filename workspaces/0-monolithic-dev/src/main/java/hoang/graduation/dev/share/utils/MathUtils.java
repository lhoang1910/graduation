package hoang.graduation.dev.share.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathUtils {

    // cộng hai số
    public static double add(double a, double b) {
        return round(a + b);
    }

    // trừ hai số
    public static double subtract(double a, double b) {
        return round(a - b);
    }

    // nhân hai số
    public static double multiply(double a, double b) {
        return round(a * b);
    }

    // chia hai số
    public static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Không thể chia cho 0");
        }
        return round(a / b);
    }

    // tính phần trăm
    public static double calculatePercentage(double value, double total) {
        if (total == 0) {
            throw new ArithmeticException("Tổng không thể bằng 0");
        }
        return round((value / total) * 100);
    }

    // tính điểm
    public static double calculateScore(double correctAmount, double total) {
        if (total == 0) {
            throw new ArithmeticException("Tổng không thể bằng 0");
        }
        return round((10 / total) * correctAmount);
    }

    // tính điểm trung bình
    public static double calculateAverage(List<Double> scores) {
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("Danh sách điểm trống");
        }
        double sum = 0;
        for (double score : scores) {
            sum += score;
        }
        return round(sum / scores.size());
    }

    // lấy điểm cao nhất
    public static double getMaxScore(List<Double> scores) {
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("Danh sách điểm trống");
        }
        return round(Collections.max(scores));
    }

    // lấy điểm thấp nhất
    public static double getMinScore(List<Double> scores) {
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("Danh sách điểm trống");
        }
        return round(Collections.min(scores));
    }

    // lấy mức điểm mà nhiều người đạt nhất (mode)
    public static double getMode(List<Double> scores) {
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("Danh sách điểm trống");
        }
        Map<Double, Integer> frequencyMap = new HashMap<>();
        for (double score : scores) {
            frequencyMap.put(score, frequencyMap.getOrDefault(score, 0) + 1);
        }
        double mode = scores.get(0);
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return round(mode);
    }

    // phân phối điểm theo các khoảng từ "0-1", "1-2", ..., "9-10" (phổ điểm)
    public static Map<String, Double> getScoreSpectrum(List<Double> scores) {
        Map<String, Double> scoreSpectrum = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String key = i + "-" + (i + 1);
            scoreSpectrum.put(key, 0.0);
        }
        for (double score : scores) {
            int range = (int) Math.floor(score);
            if (range == 10) {
                scoreSpectrum.put("9-10", scoreSpectrum.get("9-10") + 1);
            } else {
                String key = range + "-" + (range + 1);
                scoreSpectrum.put(key, scoreSpectrum.get(key) + 1);
            }
        }

        return scoreSpectrum;
    }

    // tính thứ hạng
    public static int getRank(double newScore, List<Double> scores) {
        Collections.sort(scores, Collections.reverseOrder());
        return scores.indexOf(newScore) + 1;
    }

    // làm tròn đến 2 chữ số thập phân
    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

