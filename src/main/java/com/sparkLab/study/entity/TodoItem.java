package com.sparkLab.study.entity;

import com.sparkLab.study.constant.Subject;
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
public class TodoItem extends BaseTime{

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
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL)
    private List<StudyTimeLog> studyTimeLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plannerId", nullable = false)
    private Planner planner;
}
