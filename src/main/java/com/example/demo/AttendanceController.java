package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository repo;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails user) {
        return "勤怠システム起動OK - ログイン中: " + user.getUsername();
    }

    @PostMapping("/clock-in")
    public String clockIn(@AuthenticationPrincipal UserDetails user) {
        Attendance a = new Attendance();
        a.setClockIn(LocalDateTime.now());
        a.setUsername(user.getUsername());
        repo.save(a);
        return user.getUsername() + " が出勤しました";
    }

    @PostMapping("/clock-out")
    public String clockOut(@AuthenticationPrincipal UserDetails user) {
        List<Attendance> list = repo.findByUsernameOrderByIdDesc(user.getUsername());
        if (list.isEmpty()) {
            return "出勤記録がありません";
        }
        Attendance latest = list.get(0);
        if (latest.getClockOut() != null) {
            return "すでに退勤済みです";
        }
        latest.setClockOut(LocalDateTime.now());
        repo.save(latest);
        return user.getUsername() + " が退勤しました";
    }

    @GetMapping("/list")
    public List<Attendance> list(@AuthenticationPrincipal UserDetails user) {
        return repo.findByUsernameOrderByIdDesc(user.getUsername());
    }

    @GetMapping("/csrf-token")
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }
}
