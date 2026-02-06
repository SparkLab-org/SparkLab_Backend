package com.sparkLab.study.dto.feedback;

import com.sparkLab.study.constant.FeedbackSection;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedbackDraftResponse {

    private String draftText;
    private List<FeedbackSection> sections;
}
