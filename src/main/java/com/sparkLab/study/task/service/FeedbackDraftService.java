package com.sparkLab.study.task.service;

import com.sparkLab.study.task.config.FeedbackTemplateProperties;
import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.task.constant.FeedbackSection;
import com.sparkLab.study.task.dto.feedback.FeedbackDraftRequest;
import com.sparkLab.study.task.dto.feedback.FeedbackDraftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class FeedbackDraftService {

    private final FeedbackTemplateProperties templates;

    public FeedbackDraftResponse generateDraft(FeedbackDraftRequest request) {
        FeedbackDraftRequest safeRequest = request != null ? request : new FeedbackDraftRequest();
        List<FeedbackSection> sections = resolveSections(safeRequest.getSections());
        List<String> lines = new ArrayList<>();

        for (FeedbackSection section : sections) {
            String line = pickSectionLine(section, safeRequest.getSubject(), safeRequest.getWeaknessType());
            if (line != null && !line.isBlank()) {
                lines.add(line);
            }
        }

        return FeedbackDraftResponse.builder()
                .draftText(String.join("\n", lines))
                .sections(sections)
                .build();
    }

    private List<FeedbackSection> resolveSections(List<FeedbackSection> requested) {
        if (requested == null || requested.isEmpty()) {
            return List.of(
                    FeedbackSection.GREETING,
                    FeedbackSection.COACHING,
                    FeedbackSection.STUDY_TIP,
                    FeedbackSection.SUBJECT_TIP,
                    FeedbackSection.WEAKNESS_ADVICE,
                    FeedbackSection.PRAISE
            );
        }
        return requested;
    }

    private String pickSectionLine(FeedbackSection section, Subject subject, String weaknessType) {
        return switch (section) {
            case GREETING -> pickRandom(templates.getGreetings());
            case COACHING -> pickRandom(templates.getCoaching());
            case STUDY_TIP -> pickRandom(templates.getStudyTips());
            case SUBJECT_TIP -> pickSubjectTip(subject);
            case WEAKNESS_ADVICE -> pickWeaknessTip(weaknessType);
            case PRAISE -> pickRandom(templates.getPraise());
        };
    }

    private String pickSubjectTip(Subject subject) {
        Map<Subject, List<String>> subjectTips = templates.getSubjectTips();
        if (subject != null && subjectTips.containsKey(subject)) {
            return pickRandom(subjectTips.get(subject));
        }
        if (subjectTips.containsKey(Subject.ALL)) {
            return pickRandom(subjectTips.get(Subject.ALL));
        }
        return pickRandom(flattenValues(subjectTips));
    }

    private String pickWeaknessTip(String weaknessType) {
        Map<String, List<String>> weaknessTips = templates.getWeaknessTips();
        if (weaknessType != null && weaknessTips.containsKey(weaknessType)) {
            return pickRandom(weaknessTips.get(weaknessType));
        }
        return pickRandom(flattenValues(weaknessTips));
    }

    private List<String> flattenValues(Map<?, List<String>> map) {
        return map.values().stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    private String pickRandom(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(values.size());
        return values.get(index);
    }
}
