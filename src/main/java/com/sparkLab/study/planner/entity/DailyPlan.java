package com.sparkLab.study.planner.entity;

import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "dailyPlan")
    private Set<TodoItem> todoItems = new HashSet<>();

    @OneToMany(mappedBy = "dailyPlan")
    private Set<Routine> routine = new HashSet<>();


    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void assignMentee(Long menteeId) {
        this.mentee  = new Mentee();
        this.mentee.setMenteeId(menteeId);
    }
}
