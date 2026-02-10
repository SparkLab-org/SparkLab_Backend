package com.sparkLab.study.user.dto;

import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class MenteeMyPageRes {

    private Long menteeId;
    private String accountId;
    private ActiveLevel activeLevel;

    private Long mentorId;
    private String mentorAccountId;
    private Subject mentorSubject;

    private int totalTodoCount;
    private int completedTodoCount;
    private double achievementRate;
}
