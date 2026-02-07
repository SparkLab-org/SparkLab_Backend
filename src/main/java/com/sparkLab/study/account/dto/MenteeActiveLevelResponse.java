package com.sparkLab.study.account.dto;

import com.sparkLab.study.account.constant.ActiveLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenteeActiveLevelResponse {
    private Long menteeId;
    private ActiveLevel activeLevel;
}
