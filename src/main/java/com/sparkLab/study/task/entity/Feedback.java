package com.sparkLab.study.task.entity;

import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.planner.entity.TodoItem;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private String title;
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
