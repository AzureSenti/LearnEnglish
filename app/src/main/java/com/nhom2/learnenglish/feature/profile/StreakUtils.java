package com.nhom2.learnenglish.feature.profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StreakUtils {

    /**
     * Tính toán chuỗi ngày học liên tiếp (Streak).
     * @param datesInMillis Danh sách timestamp các lần học/ôn tập.
     * @return Số ngày liên tiếp.
     */
    public static int calculateStreak(List<Long> datesInMillis) {
        if (datesInMillis == null || datesInMillis.isEmpty()) return 0;

        // 1. Loại bỏ trùng lặp trong cùng 1 ngày và chuẩn hóa về 00:00:00
        Set<Long> uniqueDays = new HashSet<>();
        for (Long timestamp : datesInMillis) {
            if (timestamp == null) continue;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            setStartOfDay(cal);
            uniqueDays.add(cal.getTimeInMillis());
        }

        // 2. Chuyển sang List và sắp xếp giảm dần (mới nhất lên đầu)
        List<Long> sortedDays = new ArrayList<>(uniqueDays);
        Collections.sort(sortedDays, Collections.reverseOrder());

        if (sortedDays.isEmpty()) return 0;

        // 3. Kiểm tra điều kiện bắt đầu (Hôm nay hoặc Hôm qua)
        Calendar today = Calendar.getInstance();
        setStartOfDay(today);
        long todayMillis = today.getTimeInMillis();

        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        long yesterdayMillis = yesterday.getTimeInMillis();

        long latestStudyDay = sortedDays.get(0);

        // Nếu ngày gần nhất không phải hôm nay cũng không phải hôm qua -> Streak = 0
        if (latestStudyDay != todayMillis && latestStudyDay != yesterdayMillis) {
            return 0;
        }

        // 4. Đếm chuỗi ngày liên tiếp
        int streak = 1;
        long oneDayMillis = 24 * 60 * 60 * 1000L;

        for (int i = 0; i < sortedDays.size() - 1; i++) {
            long current = sortedDays.get(i);
            long next = sortedDays.get(i + 1);

            // Khoảng cách giữa 2 ngày phải là đúng 1 ngày
            if (current - next == oneDayMillis) {
                streak++;
            } else if (current - next < oneDayMillis) {
                // Đã xử lý uniqueDays nên không xảy ra, nhưng để an toàn
                continue;
            } else {
                break; // Bị ngắt quãng
            }
        }

        return streak;
    }

    private static void setStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
