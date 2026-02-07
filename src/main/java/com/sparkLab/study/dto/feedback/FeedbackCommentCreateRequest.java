package com.sparkLab.study.dto.feedback;

import com.sparkLab.study.constant.FeedbackCommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCommentCreateRequest {

    @NotNull
    private FeedbackCommentType type;

    @NotBlank
    private String content;
}
