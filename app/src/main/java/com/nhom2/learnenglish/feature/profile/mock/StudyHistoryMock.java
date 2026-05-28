package com.nhom2.learnenglish.feature.profile.mock;

/**
 * Lớp đại diện cho dữ liệu lịch sử học tập giả lập.
 */
public class StudyHistoryMock {
    private long date; // Timestamp của ngày học
    private int xpEarned; // Số XP đạt được trong ngày
    private long timeSpentMillis; // Thời gian học (miliseconds)

    public StudyHistoryMock(long date, int xpEarned, long timeSpentMillis) {
        this.date = date;
        this.xpEarned = xpEarned;
        this.timeSpentMillis = timeSpentMillis;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public long getTimeSpentMillis() {
        return timeSpentMillis;
    }

    public void setTimeSpentMillis(long timeSpentMillis) {
        this.timeSpentMillis = timeSpentMillis;
    }
}
