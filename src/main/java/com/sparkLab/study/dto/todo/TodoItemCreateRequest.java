package com.sparkLab.study.dto.todo;

import com.sparkLab.study.constant.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemCreateRequest {

    @NotNull(message = "plannerId는 필수입니다")
    private Long plannerId;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private LocalDate targetDate;
    private Subject subject;
    private String type;
    private String goal;
    private String materialType;
    private String materialUrl;
    private Integer plannedMinutes;
}
