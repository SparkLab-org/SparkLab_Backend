package com.sparkLab.study.user.dto;

import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenteeActiveLevelResponse {
    private Long menteeId;
    private ActiveLevel activeLevel;
}
