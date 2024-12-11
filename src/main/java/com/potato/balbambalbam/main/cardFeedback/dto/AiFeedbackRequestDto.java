package com.potato.balbambalbam.main.cardFeedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiFeedbackRequestDto {
    @NotBlank
    public String userAudio;
    @NotBlank
    public String correctAudio;
    @NotBlank
    @Size(max = 35)
    public String pronunciation;

}
