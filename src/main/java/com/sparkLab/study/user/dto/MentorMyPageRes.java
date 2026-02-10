package com.sparkLab.study.user.dto;

import com.sparkLab.study.common.constant.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorMyPageRes {

    private Long mentorId;
    private String accountId;
    private Subject subject;

    private int menteeCount;
}

