package com.example.bejipsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScriptController {
    @Autowired private ScriptRepository scriptRepository;

    @PostMapping("/script")
    public ResponseEntity<?> printScript(@RequestBody ScriptRequestDto scriptRequestDto) {
        List<Script> datas = null;
        switch (scriptRequestDto.getCharacter()) {
            case 1:
                datas = scriptRepository.findCharacter1();
                break;
            case 2:
                datas = scriptRepository.findCharacter2();
                break;
            case 3:
                datas = scriptRepository.findCharacter3();
                break;
            case 4:
                datas = scriptRepository.findCharacter4();
                break;
        }
        List<ScriptDto> responses = new ArrayList<>();
        for (Script data : datas) {
            Boolean isChoice = null;
            if (data.getIsChoice() != null) isChoice = (data.getIsChoice().equals("TRUE"))? true : false;
            responses.add(new ScriptDto(
                    data.getDialogueId(),
                    data.getSceneType(),
                    data.getDialogueType(),
                    isChoice,
                    data.getSpeakerName(),
                    data.getDialogueText(),
                    data.getNextDialogueId(),
                    data.getScoreChange()
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
