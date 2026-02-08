package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignmentSubmissions")
@Getter
@Setter
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
    private String comment;
    private String status;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
