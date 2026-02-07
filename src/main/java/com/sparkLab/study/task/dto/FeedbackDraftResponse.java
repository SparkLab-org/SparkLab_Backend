package com.sparkLab.study.task.dto;

import com.sparkLab.study.task.constant.FeedbackSection;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedbackDraftResponse {

    private String draftText;
    private List<FeedbackSection> sections;
}
