package com.sparkLab.study.activity.constant;

import lombok.Getter;

@Getter
public enum QnaStatus {
    PENDING("확인중"),
    ANSWERED("답변완료");

    private final String displayName;

    QnaStatus(String displayName) {
        this.displayName = displayName;
    }
}
