package com.sparkLab.study.task.entity;

import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.user.entity.Mentee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbackBookmarks", uniqueConstraints = @UniqueConstraint(columnNames = {"menteeId", "feedbackId"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackBookmark extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedbackId", nullable = false)
    private Feedback feedback;
}
