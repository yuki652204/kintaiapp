package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // テスト1: 未ログインはログイン画面にリダイレクト
    @Test
    void 未ログインはリダイレクトされる() throws Exception {
        mockMvc.perform(get("/attendance/list"))
            .andExpect(status().is3xxRedirection());
    }

    // テスト2: 出勤APIが正常に動作する
    @Test
    @WithMockUser(username = "yamada")
    void 出勤APIが正常に動作する() throws Exception {
        mockMvc.perform(post("/attendance/clock-in"))
            .andExpect(status().isOk())
            .andExpect(content().string("yamada が出勤しました"));
    }

    // テスト3: 出勤後に一覧が1件になる
    @Test
    @WithMockUser(username = "yamada")
    void 出勤後に一覧が1件になる() throws Exception {
        mockMvc.perform(post("/attendance/clock-in"));
        mockMvc.perform(get("/attendance/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    // テスト4: 出勤→退勤が正常に動作する
    @Test
    @WithMockUser(username = "yamada")
    void 出勤退勤が正常に動作する() throws Exception {
        mockMvc.perform(post("/attendance/clock-in"));
        mockMvc.perform(post("/attendance/clock-out"))
            .andExpect(status().isOk())
            .andExpect(content().string("yamada が退勤しました"));
    }

    // テスト5: 出勤前に退勤するとエラーメッセージ
    @Test
    @WithMockUser(username = "yamada")
    void 出勤前退勤はエラーになる() throws Exception {
        mockMvc.perform(post("/attendance/clock-out"))
            .andExpect(status().isOk())
            .andExpect(content().string("出勤記録がありません"));
    }

    // テスト6: ユーザーごとにデータが分離されている
    @Test
    @WithMockUser(username = "tanaka")
    void tanakaはyamadaのデータを見られない() throws Exception {
        mockMvc.perform(get("/attendance/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
