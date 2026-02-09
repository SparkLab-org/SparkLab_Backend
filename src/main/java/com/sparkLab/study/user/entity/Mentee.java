package com.sparkLab.study.user.entity;

import com.sparkLab.study.user.constant.ActiveLevel;
import com.sparkLab.study.task.entity.Feedback;
import com.sparkLab.study.acheivement.entity.ProgressStatics;
import com.sparkLab.study.planner.entity.TodoItem;
import com.sparkLab.study.security.auth.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "mentees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mentee {

    @Id
    @Column(name = "menteeId")
    private Long menteeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    private Account account;

    // <기획안 필수기능 요구사항>
    // 한 명의 멘토가 최대 11명의 멘티 담당 1:N구조
    @ManyToOne
    @JoinColumn(name = "mentorId", referencedColumnName = "mentorId")
    private Mentor mentorId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActiveLevel activeLevel;

    // 과목별&총  달성률
    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL)
    private List<ProgressStatics> progressStatics;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL)
    private List<TodoItem> todoItems;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    @PrePersist
    private void applyDefaultActiveLevel() {
        if (activeLevel == null) {
            activeLevel = ActiveLevel.NORMAL;
        }
    }
}