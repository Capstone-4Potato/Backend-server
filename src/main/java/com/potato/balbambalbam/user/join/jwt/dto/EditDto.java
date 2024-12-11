package com.potato.balbambalbam.user.join.jwt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditDto {

    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Integer age;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Byte gender;

    public EditDto(String name, Integer age, Byte gender){
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
