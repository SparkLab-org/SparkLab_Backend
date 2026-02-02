package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "menteeId")
    private Mentee mentee;

    private LocalDateTime targetDate;
    private String subject;
    private Boolean isImportant;
    private String summary;

    @Lob
    private String content;


    @ManyToOne
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;
}
