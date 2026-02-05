package com.sparkLab.study.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackCreateRequest {

    @NotNull(message = "mentorId는 필수입니다")
    private Long mentorId;

    @NotNull(message = "menteeId는 필수입니다")
    private Long menteeId;

    private Long todoItemId;

    private LocalDateTime targetDate;

    private Boolean isImportant;

    @NotBlank(message = "summary는 필수입니다")
    private String summary;

    @NotBlank(message = "content는 필수입니다")
    private String content;
}
