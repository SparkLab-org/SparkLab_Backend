package com.sparkLab.study.activity.dto.qna;

import com.sparkLab.study.activity.constant.QnaStatus;
import com.sparkLab.study.common.constant.Subject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class QnaResponse {
    private Long qnaId;
    private Subject subject;
    private String title;
    private String content;
    private String attachmentUrl;
    private Long menteeId;
    private QnaStatus status;
    private String statusDisplayName;
    private LocalDateTime answeredAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<QnaReplyResponse> replies;
}
