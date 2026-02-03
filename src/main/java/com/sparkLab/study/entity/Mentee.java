package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.List;

@Entity
@Table(name = "mentees")
@Getter
public class Mentee {

    @Id
    @Column(name = "menteeId")
    private Long menteeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "mentorId", referencedColumnName = "mentorId")
    private Mentor mentorId;

    @Column(name = "learningGoals", columnDefinition = "TEXT")
    private String learningGoals;

    @Column(name = "progressStatus", length = 50)
    private String progressStatus;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL)
    private List<TodoItem> todoItems;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}