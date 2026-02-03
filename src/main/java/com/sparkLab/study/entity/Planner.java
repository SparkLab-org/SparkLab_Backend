package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "planners",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mentee_id", "plan_date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planner extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plannerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @Column(nullable = false)
    private LocalDate planDate;

    @Column(columnDefinition = "TEXT")
    private String comment; //자기 평가

    @OneToMany(mappedBy = "planner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TodoItem> todoItems = new ArrayList<>();


}
