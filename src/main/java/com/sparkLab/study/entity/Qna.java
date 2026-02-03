package com.sparkLab.study.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionId")
    private Long qnaId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routineId")
    private Routine routine; // nullable

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 답변시
    @Column(nullable = false)
    private String status = "open";

    private LocalDateTime answeredAt;

}
