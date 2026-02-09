package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import com.sparkLab.study.constant.Subject;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private LocalDateTime targetDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    private String summary;
    private String importantComment;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;

    @ManyToOne
    @JoinColumn(name = "assignmentId")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;
}
