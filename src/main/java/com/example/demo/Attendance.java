package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getClockIn() { return clockIn; }
    public void setClockIn(LocalDateTime clockIn) { this.clockIn = clockIn; }
    public LocalDateTime getClockOut() { return clockOut; }
    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }

    // 勤務時間（分）を返す
    public Long getWorkMinutes() {
        if (clockIn == null || clockOut == null) return null;
        return Duration.between(clockIn, clockOut).toMinutes();
    }

    // 残業時間（分）を返す（8時間超え）
    public Long getOvertimeMinutes() {
        Long work = getWorkMinutes();
        if (work == null) return null;
        long overtime = work - 480; // 480分 = 8時間
        return overtime > 0 ? overtime : 0L;
    }
}
