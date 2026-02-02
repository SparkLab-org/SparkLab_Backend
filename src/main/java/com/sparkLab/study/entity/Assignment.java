package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "assignments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;

    private String materialType;
    private String materialTitle;
    private String materialFileUrl;


    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> submissions;
}
