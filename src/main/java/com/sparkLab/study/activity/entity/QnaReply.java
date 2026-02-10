package com.sparkLab.study.activity.entity;

import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.user.entity.Mentor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_replies")
@Getter
@Setter
@NoArgsConstructor
public class QnaReply extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qnaReplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", nullable = false)
    private Qna qna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentorId", nullable = false)
    private Mentor mentor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
