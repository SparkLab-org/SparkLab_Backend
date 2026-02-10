package com.sparkLab.study.activity.dto.qna;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaUpdateRequest {

    private String title;
    private String content;
    private String attachmentUrl;
}
