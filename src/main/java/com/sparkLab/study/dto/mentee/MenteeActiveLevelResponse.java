package com.sparkLab.study.dto.mentee;

import com.sparkLab.study.constant.ActiveLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenteeActiveLevelResponse {
    private Long menteeId;
    private ActiveLevel activeLevel;
}
