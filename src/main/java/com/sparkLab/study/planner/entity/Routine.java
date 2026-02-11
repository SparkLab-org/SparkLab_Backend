package com.sparkLab.study.planner.entity;

import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Routine extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routineId")
    private Long routineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(name = "targetMinutes")
    private Integer targetMinutes;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyPlanId", nullable = false)
    private DailyPlan dailyPlan;

}
