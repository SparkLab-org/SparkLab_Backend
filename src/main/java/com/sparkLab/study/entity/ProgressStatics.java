package com.sparkLab.study.entity;

import com.sparkLab.study.constant.Subject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progressStatics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProgressStatics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    private Subject subject;
    private int completedCount;   // 완료한 과제 수
    private int totalCount;       // 총 과제 수
    private double achievementRate; // 완료율 (0~100)
}
