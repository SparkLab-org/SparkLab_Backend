package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "studyTimeLogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyTimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StudyTimeLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todoItem", nullable = false)
    private TodoItem todoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @Column(nullable = false)
    private Integer studiedMinutes;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }

    public String getFormattedStudyTime() {
        int hours = studiedMinutes / 60;
        int minutes = studiedMinutes % 60;
        return hours > 0
                ? String.format("%d시간 %d분", hours, minutes)
                : String.format("%d분", minutes);
    }
}
