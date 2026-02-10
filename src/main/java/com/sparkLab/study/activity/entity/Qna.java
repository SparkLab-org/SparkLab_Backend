package com.sparkLab.study.activity.entity;

import com.sparkLab.study.activity.constant.QnaStatus;
import com.sparkLab.study.common.entity.BaseTime;
import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.user.entity.Mentee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 기존 설계 참고용 (실제 사용 X)
// import com.sparkLab.study.planner.entity.Routine;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionId")
    private Long qnaId;

    // 기존 필드 (삭제 대신 보존) ------------------------------
    // @Column(name = "userId", nullable = false)
    // private Long userId;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "routineId")
    // private Routine routine; // nullable
    //
    // // 답변시
    // @Column(nullable = false)
    // private String legacyStatus = "open";
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Subject subject;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 자료 첨부 파일 경로 또는 URL (선택) */
    @Column(length = 500)
    private String attachmentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menteeId", nullable = false)
    private Mentee mentee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QnaStatus status = QnaStatus.PENDING;

    private LocalDateTime answeredAt;

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaReply> replies = new ArrayList<>();

    public void markAnswered() {
        this.status = QnaStatus.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }
}
