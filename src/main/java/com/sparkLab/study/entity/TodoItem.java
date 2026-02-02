package com.sparkLab.study.entity;

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

    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;

    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    private LocalDate targetDate;
    private String title;
    private String subject;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plannerId", nullable = false)
    private Planner planner;
}
