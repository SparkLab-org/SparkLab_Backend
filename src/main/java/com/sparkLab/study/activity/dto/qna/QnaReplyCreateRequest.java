package com.sparkLab.study.activity.dto.qna;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaReplyCreateRequest {

    @NotBlank(message = "답변 내용은 필수입니다")
    private String content;
}
