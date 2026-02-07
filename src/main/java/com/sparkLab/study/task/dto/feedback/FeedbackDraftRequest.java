package com.sparkLab.study.task.dto.feedback;

import com.sparkLab.study.task.constant.FeedbackSection;
import com.sparkLab.study.common.constant.Subject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedbackDraftRequest {

    private Subject subject;
    private String weaknessType;
    private List<FeedbackSection> sections;
}
