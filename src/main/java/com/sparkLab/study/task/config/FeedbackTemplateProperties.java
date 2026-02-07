package com.sparkLab.study.task.config;

import com.sparkLab.study.common.constant.Subject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "feedback-templates")
public class FeedbackTemplateProperties {

    private List<String> greetings = new ArrayList<>();
    private List<String> coaching = new ArrayList<>();
    private List<String> studyTips = new ArrayList<>();
    private Map<Subject, List<String>> subjectTips = new EnumMap<>(Subject.class);
    private Map<String, List<String>> weaknessTips = new LinkedHashMap<>();
    private List<String> praise = new ArrayList<>();
}
