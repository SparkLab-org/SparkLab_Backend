package com.sparkLab.study.user.dto;


import com.sparkLab.study.user.constant.ActiveLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeSummaryResponse {

    private Long menteeId;
    private String accountId;
    private ActiveLevel activeLevel;
}
