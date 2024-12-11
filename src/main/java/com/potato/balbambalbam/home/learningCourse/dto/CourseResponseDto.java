package com.potato.balbambalbam.home.learningCourse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "코스 리스트 Response")
public class CourseResponseDto {
    private List<Course> courseList;

    public CourseResponseDto(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Getter
    @Setter
    @Schema(description = "각 코스에 대한 정보")
    public static class Course {
        long id;
        int totalNumber;
        int completedNumber;

        public Course(long id, int totalNumber, int completedNumber) {
            this.id = id;
            this.totalNumber = totalNumber;
            this.completedNumber = completedNumber;
        }
    }
}
