package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository repo;

    @GetMapping("/")
    public String home() {
        return "勤怠システム起動OK";
    }

    @PostMapping("/clock-in")
    public String clockIn() {
        Attendance a = new Attendance();
        a.setClockIn(LocalDateTime.now());
        repo.save(a);
        return "出勤しました";
    }

    @PostMapping("/clock-out")
    public String clockOut() {
        List<Attendance> list = repo.findAll();
        if (list.isEmpty()) {
            return "出勤記録がありません";
        }
        Attendance latest = list.get(list.size() - 1);
        if (latest.getClockOut() != null) {
            return "すでに退勤済みです";
        }
        latest.setClockOut(LocalDateTime.now());
        repo.save(latest);
        return "退勤しました";
    }

    @GetMapping("/list")
    public List<Attendance> list() {
        return repo.findAll();
    }
}
