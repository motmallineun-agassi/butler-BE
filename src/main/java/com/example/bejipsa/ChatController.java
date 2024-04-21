package com.example.bejipsa;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ChatController {
    // 세션별로 메시지 저장할 리스트 찾기
    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequestDto chatRequestDto, HttpServletRequest httpServletRequest) {
        // 0. 이전 대화 목록 가져오기
        String sessionId = httpServletRequest.getSession().getId();
        List<Map<String, String>> previousMessages = sessionManager.getPreviousMessages(sessionId); // 새 리스트

        // 1. Map 형식으로 변환 후 리스트에 넣기
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", chatRequestDto.getQuestion());
        previousMessages.add(userMessage);

        // 2. 전체 Map Json 형식으로 변환
        Gson gson = new Gson();
        String json = gson.toJson(previousMessages); // 여기까지 OK

        // 3. Processbuilder로 응답 받기
        String response = "";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "/Users/weirdo/Downloads/bejipsa/src/main/python/chat_with_memory.py", json);
            Process process = processBuilder.start();

            // 1. 오류 메시지 출력
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String errorMessage = "";
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorMessage += errorLine;
            }

            // 2. 결과 출력
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response += line;
            }

            // 3. 종료 처리
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.info("Exitcode: " + Integer.toString(exitCode) + "\nErrorMessage: " + errorMessage);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        // 4. 받은 응답 Map 형식으로 변환
        Map<String, String> butlerMessage = new HashMap<>();
        butlerMessage.put("role", "assistant");
        butlerMessage.put("content", response);

        // 5. previousMessage에 추가해주기
        previousMessages.add(butlerMessage);
        sessionManager.savePreviousMessages(sessionId, previousMessages); // 세션별로 저장

        // 6. 리턴
        ChatResponseDto chatResponseDto = new ChatResponseDto(response);
        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDto);
    }
}
