package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assignment_submissions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmission extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "assignmentId")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;

    private String imageUrl;
    private String status;

}
