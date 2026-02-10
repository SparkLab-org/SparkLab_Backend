package com.sparkLab.study.planner.service;

import com.sparkLab.study.planner.entity.ProgressStatics;
import com.sparkLab.study.planner.repository.DailyPlanRepository;
import com.sparkLab.study.planner.repository.DailyPlanSummaryRepository;
import com.sparkLab.study.user.entity.Mentee;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DailyPlanSummaryBatchService {

    private final DailyPlanRepository dailyPlanRepository;
    private final DailyPlanSummaryRepository summaryRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void batchDailyPlanSummary() {

        LocalDate today = LocalDate.now();
        List<Mentee> allMentees = dailyPlanRepository.findAllMenteesWithPlans();

        for (Mentee mentee : allMentees) {
            List<Object[]> summaryData = dailyPlanRepository.findDailyTodoSummary(
                    mentee.getMenteeId(), today.minusDays(7), today
            );

            for (Object[] obj : summaryData) {
                LocalDate planDate = (LocalDate) obj[0];
                int totalCount = ((Long) obj[1]).intValue();
                int completedCount = ((Long) obj[2]).intValue();
                double achievementRate = totalCount > 0 ? completedCount * 100.0 / totalCount : 0.0;

                // DB에 이미 존재하면 업데이트
                int updated = summaryRepository.updateSummary(
                        mentee, planDate, totalCount, completedCount, achievementRate
                );

                // 업데이트가 0이면 insert
                if (updated == 0) {
                    summaryRepository.save(
                            ProgressStatics.builder()
                                    .mentee(mentee)
                                    .planDate(planDate)
                                    .totalCount(totalCount)
                                    .completedCount(completedCount)
                                    .achievementRate(achievementRate)
                                    .build()
                    );
                }
            }
        }
    }
}
