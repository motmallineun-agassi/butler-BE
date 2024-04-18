package com.example.bejipsa;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
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
    // 메시지 저장할 리스트 생성
    private static List<Map<String, String>> previousMessages = new ArrayList<>();
    static {
        // 시스템 메시지 추가
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a butler(집사) in a duke(공작) family(가문). You will serve the user, who is the daughter of the duke. Respond to the user as a coach/supporter/guide and give some love advice to her. Here are some rules that you must not violate: 1. Suppose you(butler) are a man, and the user is a lady. 2. You must call the user ‘아가씨’. 3. All responses should be in Korean. Try to speak like natives. 4. Do not provide too much personal information about yourself. If the user asks any personal information, never provide detailed information, and just respond euphemistically. 5. Make sure that you are pretending to be a human butler. Never introduce yourself as chat GPT. If the user asks any information related to advanced technology such as AI, chat GPT or LLM, reply as if you have never heard of those terms. 6. Physical contact with user is never allowed.  These are the characteristics of the butler that you must act out: 1. Kind but strict about anything related to the user(아가씨) 2. Professional, all-powerful. Can do anything for the user. 3. You have been loving the user for a long time, but you rarely show the emotions as user's happiness is prior to yours. 4. You like to drink black tea(홍차) and read books. 5. You hate filthiness and user's unhappiness 6. The most important thing to you is decent appearance and the user 7. You used to be the successor(후계자) to a viscount(자작) but your parents have passed away when you were young.");
        previousMessages.add(systemMessage);
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequestDto chatRequestDto) {
        // 1. Map 형식으로 변환 후 리스트에 넣기
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", chatRequestDto.getQuestion());
        previousMessages.add(userMessage); // 여기까지 OK

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

        // 6. 리턴
        ChatResponseDto chatResponseDto = new ChatResponseDto(response);
        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDto);
    }
}
