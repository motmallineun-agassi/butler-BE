package com.example.bejipsa;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    @GetMapping("/generateSessionId")
    public ResponseEntity<?> generateSessionId(HttpServletRequest request) {
        // 1. 요청에 대한 세션 가져오기 (없으면 새로 생성)
        HttpSession session = request.getSession(true);
        // 2. 세션 ID 가져오기
        String sessionId = session.getId();
        SessionResponseDto sessionResponseDto = new SessionResponseDto(sessionId);
        // 3. 응답
        return (sessionResponseDto != null)?
                ResponseEntity.status(HttpStatus.OK).body(sessionResponseDto):
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("세션ID 생성에 실패했습니다.");
    }
}
