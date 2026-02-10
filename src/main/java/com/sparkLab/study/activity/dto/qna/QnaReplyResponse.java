package com.sparkLab.study.activity.dto.qna;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QnaReplyResponse {
    private Long qnaReplyId;
    private Long mentorId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
