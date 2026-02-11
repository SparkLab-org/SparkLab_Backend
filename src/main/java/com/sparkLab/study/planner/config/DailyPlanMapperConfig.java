package com.sparkLab.study.planner.config;

import com.sparkLab.study.planner.dto.dailyPlan.DailyPlanRes;
import com.sparkLab.study.planner.entity.DailyPlan;
import com.sparkLab.study.planner.entity.Routine;
import com.sparkLab.study.planner.entity.TodoItem;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DailyPlanMapperConfig {

    @Bean
    public ModelMapper dailyPlanMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(ctx -> {
            DailyPlan source = ctx.getSource();

            return DailyPlanRes.builder()
                    .dailyPlanId(source.getDailyPlanId())
                    .planDate(source.getPlanDate())
                    .comment(source.getComment())
                    .todos(
                            source.getTodoItems().stream()
                                    .map(TodoItem::getTodoItemId)
                                    .toList()
                    )
                    .routines(
                            source.getRoutine().stream()
                                    .map(Routine::getRoutineId)
                                    .toList()
                    )
                    .build();
        }, DailyPlan.class, DailyPlanRes.class);

        return modelMapper;
    }

}

