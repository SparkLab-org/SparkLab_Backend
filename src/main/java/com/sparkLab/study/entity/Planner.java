package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
        name = "planners",
        uniqueConstraints = @UniqueConstraint(columnNames = {"menteeId", "planDate"})
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
    private String comment;

    @OneToMany(mappedBy = "planner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TodoItem> todoItems = new ArrayList<>();



}
