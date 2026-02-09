package com.sparkLab.study.planner.entity;

import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "dailyPlans",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mentee_id", "plan_date"})
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPlan extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @Column(nullable = false)
    private LocalDate planDate;

    @Column(columnDefinition = "TEXT")
    private String comment; //자기 평가

    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TodoItem> todoItems = new ArrayList<>();

    public void updateComment(String comment) {
        this.comment = comment;
    }


}
