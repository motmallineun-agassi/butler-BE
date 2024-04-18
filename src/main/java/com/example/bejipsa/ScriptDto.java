package com.example.bejipsa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ScriptDto {
    private Integer dialogueId;
    private String sceneType;
    private String dialogueType;
    private Boolean isChoice;
    private String speakerName;
    private String dialogueText;
    private Integer nextDialogueId;
    private Integer scoreChange;
}
