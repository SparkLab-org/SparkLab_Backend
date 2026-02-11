package com.sparkLab.study.planner.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparkLab.study.planner.entity.DailyPlan;
import com.sparkLab.study.planner.entity.QDailyPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyPlanRepositoryImpl implements DailyPlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DailyPlan> findByMenteeAndPlanDate(
            Long menteeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        QDailyPlan dp = QDailyPlan.dailyPlan;

        return queryFactory
                .selectFrom(dp)
                .distinct()
                .leftJoin(dp.todoItems).fetchJoin()
                .leftJoin(dp.routine).fetchJoin()
                .where(
                        dp.mentee.menteeId.eq(menteeId),
                        dp.planDate.between(startDate, endDate)
                )
                .orderBy(dp.planDate.asc())
                .fetch();
    }
}
