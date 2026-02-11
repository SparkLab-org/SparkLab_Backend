package com.sparkLab.study.user.dto;

import com.sparkLab.study.common.constant.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MentorRes {

    private Long mentorId;
    private Subject subject;
    List<Long> menteeIds;
    private int menteeCount;
}

