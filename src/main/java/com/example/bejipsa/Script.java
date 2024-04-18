package com.example.bejipsa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name="script")
@Getter
@Setter
public class Script {
    @Id
    private Integer id;
    @Column
    private Integer dialogueId;
    @Column
    private String sceneType;
    @Column
    private String dialogueType;
    @Column
    private String isChoice;
    @Column
    private String speakerName;
    @Column
    private String dialogueText;
    @Column
    private Integer nextDialogueId;
    @Column
    private Integer scoreChange;
}
