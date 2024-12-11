package com.potato.balbambalbam.card.todayCourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter @NoArgsConstructor
@Schema(description = "Today Course Request")
public class CourseRequestDto {
    Integer courseCnt;
}
