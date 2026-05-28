package com.nhom2.learnenglish.feature.profile.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Repository cung cấp dữ liệu giả lập cho màn hình Profile.
 */
public class StudyHistoryMockRepository {

    private final List<StudyHistoryMock> mockDataList;

    public StudyHistoryMockRepository() {
        mockDataList = new ArrayList<>();
        initMockData();
    }

    /**
     * Khởi tạo 15 dòng dữ liệu cho 15 ngày gần nhất (có một vài ngày nghỉ để test streak).
     */
    private void initMockData() {
        Calendar calendar = Calendar.getInstance();
        setStartOfDay(calendar);

        // Tạo dữ liệu cho 15 ngày gần đây
        for (int i = 0; i < 15; i++) {
            // Giả lập: Nghỉ học vào ngày thứ 4 và ngày thứ 10 tính từ hôm nay
            if (i == 4 || i == 10) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                continue;
            }

            long date = calendar.getTimeInMillis();
            int xp = 50 + (int) (Math.random() * 100); // 50 - 150 XP
            long timeSpent = TimeUnit.MINUTES.toMillis(15 + (int) (Math.random() * 45)); // 15 - 60 phút

            mockDataList.add(new StudyHistoryMock(date, xp, timeSpent));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
    }

    public List<StudyHistoryMock> getMockDataList() {
        return mockDataList;
    }

    /**
     * Tính tổng XP đã đạt được.
     */
    public int getTotalXp() {
        int total = 0;
        for (StudyHistoryMock item : mockDataList) {
            total += item.getXpEarned();
        }
        return total;
    }

    /**
     * Tính tổng thời gian học (trả về milliseconds).
     */
    public long getTotalStudyTime() {
        long total = 0;
        for (StudyHistoryMock item : mockDataList) {
            total += item.getTimeSpentMillis();
        }
        return total;
    }

    /**
     * Thuật toán tính số ngày học liên tiếp (Streak).
     * Nguyên tắc: 
     * 1. Nếu hôm nay hoặc hôm qua có học -> Bắt đầu đếm ngược từ ngày đó.
     * 2. Nếu cả hôm nay và hôm qua đều không học -> Streak bị reset về 0.
     */
    public int getCurrentStreak() {
        if (mockDataList.isEmpty()) return 0;

        Calendar today = Calendar.getInstance();
        setStartOfDay(today);

        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        boolean hasToday = hasDataForDay(today);
        boolean hasYesterday = hasDataForDay(yesterday);

        // Nếu hôm qua không học VÀ hôm nay cũng chưa học -> Mất streak
        if (!hasToday && !hasYesterday) {
            return 0;
        }

        int streak = 0;
        // Bắt đầu kiểm tra từ hôm nay nếu hôm nay đã học, ngược lại kiểm tra từ hôm qua
        Calendar checkDate = hasToday ? (Calendar) today.clone() : (Calendar) yesterday.clone();

        while (hasDataForDay(checkDate)) {
            streak++;
            checkDate.add(Calendar.DAY_OF_YEAR, -1);
        }

        return streak;
    }

    private boolean hasDataForDay(Calendar targetDate) {
        for (StudyHistoryMock item : mockDataList) {
            Calendar itemDate = Calendar.getInstance();
            itemDate.setTimeInMillis(item.getDate());
            if (itemDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                itemDate.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        }
        return false;
    }

    private void setStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Mock dữ liệu cho biểu đồ cột (số lượng từ ở Level 1 -> Level 7).
     * @return List 7 số nguyên.
     */
    public List<Integer> getMockSrsChartData() {
        List<Integer> srsData = new ArrayList<>();
        srsData.add(120); // Level 1 (New)
        srsData.add(85);  // Level 2
        srsData.add(60);  // Level 3
        srsData.add(45);  // Level 4
        srsData.add(30);  // Level 5
        srsData.add(20);  // Level 6
        srsData.add(150); // Level 7 (Mastered)
        return srsData;
    }
}
