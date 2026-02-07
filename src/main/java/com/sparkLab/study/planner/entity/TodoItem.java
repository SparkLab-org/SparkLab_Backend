package com.sparkLab.study.planner.entity;

import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.task.entity.Assignment;
import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.task.entity.Feedback;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "todoItems")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItem extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoItemId;

    // 멘토가 배정
    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;

    // 멘티가 직접 설정
    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    private LocalDate targetDate;
    private String title;
    private Subject subject;
    private String type;
    private Boolean isFixed;
    private String status;
    private Integer plannedMinutes;
    private Integer actualMinutes;
    private Integer actualSeconds;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<StudyTimeLog> studyTimeLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyPlanId", nullable = false)
    private DailyPlan dailyPlan;
}
