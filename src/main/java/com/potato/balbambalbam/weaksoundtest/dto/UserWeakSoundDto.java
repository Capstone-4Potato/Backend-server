package com.potato.balbambalbam.weaksoundtest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWeakSoundDto {
    private int rank;
    private String phonemeText;

    public UserWeakSoundDto(int rank, String phonemeText){
        this.rank = rank;
        this.phonemeText = phonemeText;
    }
}
