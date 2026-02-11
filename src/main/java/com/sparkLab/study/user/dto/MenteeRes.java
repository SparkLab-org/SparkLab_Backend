package com.sparkLab.study.user.dto;

import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class MenteeRes {

    private Long menteeId;
    private ActiveLevel activeLevel;

    @Setter
    private Long mentorId;
}
