package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private Boolean isImportant;
    private String summary;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;

    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;
}
