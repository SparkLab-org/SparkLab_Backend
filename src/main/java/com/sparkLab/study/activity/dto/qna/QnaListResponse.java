package com.sparkLab.study.activity.dto.qna;

import com.sparkLab.study.activity.constant.QnaStatus;
import com.sparkLab.study.common.constant.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QnaListResponse {
    private Long qnaId;
    private Subject subject;
    private String title;
    private String content;
    private String statusDisplayName;
    private QnaStatus status;
    private LocalDateTime createTime;
}
