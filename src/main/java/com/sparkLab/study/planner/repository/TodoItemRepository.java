package com.sparkLab.study.planner.repository;

import com.sparkLab.study.planner.entity.TodoItem;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByDailyPlan_dailyPlanIdOrderByCreateTimeAsc(Long plannerId);

    List<TodoItem> findByDailyPlan_PlanDateOrderByCreateTimeAsc(LocalDate planDate);

    //List<TodoItem> findByDailyPlan_Mentee_MenteeIdAndDailyPlan_PlanDateOrderByCreateTimeAsc(Long menteeId, LocalDate planDate);

    boolean existsByTodoItemIdAndMentee_MenteeId(Long todoItemId, Long menteeId);

        @Query("""
    select ti
    from TodoItem ti
    join fetch ti.mentee me
    join fetch me.account
    left join fetch ti.mentor mo
    left join fetch mo.account
    where me.menteeId = :menteeId
    and ti.dailyPlan.planDate = :planDate
    order by ti.createTime asc
    """)
        List<TodoItem> findTodoItemsWithRelations(
                @Param("menteeId") Long menteeId,
                @Param("planDate") LocalDate planDate
        );

}
