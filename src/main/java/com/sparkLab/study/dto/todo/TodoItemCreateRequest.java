package com.sparkLab.study.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    private String subject;
    private String type;
    private Integer plannedMinutes;
}
