package com.sparkLab.study.planner.entity;

import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.user.entity.Mentee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "progressStatics")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProgressStatics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    private LocalDate planDate;
    private Subject subject;
    private int completedCount;   // 완료한 과제 수
    private int totalCount;       // 총 과제 수
    private double achievementRate; // 완료율 (0~100)
}
