package com.sparkLab.study.activity.dto.qna;

import com.sparkLab.study.common.constant.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaCreateRequest {

    @NotNull(message = "과목은 필수입니다")
    private Subject subject;

    @NotBlank(message = "질문 제목은 필수입니다")
    private String title;

    @NotBlank(message = "질문 내용은 필수입니다")
    private String content;

    private String attachmentUrl;
}
