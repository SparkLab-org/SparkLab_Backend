package com.sparkLab.study.task.entity;

import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.task.constant.FeedbackCommentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback_comments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackComment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeedbackCommentType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public static FeedbackComment of(Feedback feedback, FeedbackCommentType type, String content) {
        FeedbackComment comment = new FeedbackComment();
        comment.setFeedback(feedback);
        comment.setType(type);
        comment.setContent(content);
        return comment;
    }
}
