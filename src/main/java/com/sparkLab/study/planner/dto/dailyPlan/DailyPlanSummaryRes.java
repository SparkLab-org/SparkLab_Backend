package com.sparkLab.study.planner.dto.dailyPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * 일일 요약 응답 DTO
 * - 특정 날짜의 할일 통계를 간단히 표현
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPlanSummaryRes {

    private LocalDate planDate;                 // 계획 날짜
    private int totalCount;                     // 총 할일 수
    private int completedCount;                 // 완료한 할일 수
    private double achievementRate;             // 달성률 (%)

    // 추가 필드 (선택)
    private String dayOfWeek;                   // 요일 (MON, TUE, ...)
    private String status;                      // 상태 (COMPLETED, PENDING, INCOMPLETE)
    private String comment;                     // 일정 코멘트 (썸네일)

    /**
     * dayOfWeek 자동 계산 (Getter)
     */
    public String getDayOfWeek() {
        if (this.planDate != null) {
            return this.planDate.getDayOfWeek().toString();
        }
        return null;
    }

    /**
     * 완료 상태 여부
     */
    public boolean isCompleted() {
        return this.totalCount > 0 && this.completedCount == this.totalCount;
    }

    /**
     * 부분 완료 여부
     */
    public boolean isPartiallyCompleted() {
        return this.completedCount > 0 && this.completedCount < this.totalCount;
    }

    /**
     * 미완료 여부
     */
    public boolean isPending() {
        return this.completedCount == 0 && this.totalCount > 0;
    }
}