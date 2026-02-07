package com.sparkLab.study.entity;

import com.sparkLab.study.constant.TimeLogStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "studyTimelog",
        indexes = {
                @Index(name = "idx_timelog_account_start", columnList = "accountId, startAt"),
                @Index(name = "idx_timelog_account_stop", columnList = "accountId, stopAt"),
                @Index(name = "idx_timelog_routine", columnList = "routineId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTimeLog extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timelogId")
    private Long timelogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routineId")
    private Routine routineId;   // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    private Account account;     // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todoItemId")
    private TodoItem todoItem;   // nullable


    @Column(name = "startAt", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "stopAt")
    private LocalDateTime stopAt;

    @Column(name = "duration_sec")
    private Long durationSec;  // stop 시 계산

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeLogStatus status = TimeLogStatus.RUNNING;

}
