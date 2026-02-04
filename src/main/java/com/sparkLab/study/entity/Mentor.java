package com.sparkLab.study.entity;

import com.sparkLab.study.constant.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.List;

@Entity
@Table(name = "mentors")
@Getter
public class Mentor {

    @Id
    @Column(name = "mentorId")
    private Long mentorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    // 멘토는 멘티를 거쳐서
    @OneToMany(mappedBy = "mentorId", fetch = FetchType.LAZY)
    private List<Mentee> mentees;

    @OneToMany(mappedBy = "feedbackId", fetch = FetchType.LAZY)
    private List<Feedback> feedback;

    @OneToMany(mappedBy = "todoItemId", fetch = FetchType.LAZY)
    private List<TodoItem> todoItem;


}