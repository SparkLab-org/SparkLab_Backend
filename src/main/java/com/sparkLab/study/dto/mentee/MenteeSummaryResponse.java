package com.sparkLab.study.dto.mentee;

import com.sparkLab.study.constant.ActiveLevel;
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
