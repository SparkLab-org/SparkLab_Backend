package com.sparkLab.study.task.entity;

import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.planner.entity.TodoItem;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    // cascade = CascadeType.ALL 하면 안 됨
    @ManyToOne
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;

    @ManyToOne
    @JoinColumn(name = "mentorId")
    private Mentor mentor;

    private String materialType;
    private String materialTitle;
    private String materialFileUrl;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> submissions;
}
