package com.sparkLab.study.entity;

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

    @Column(name = "expertise", length = 255)
    private String expertise;

    @Column(name = "careerYears")
    private Integer careerYears;

    @Column(name = "availableTimes", length = 255)
    private String availableTimes;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
    private List<TodoItem> assignedTodos;


}