package com.sparkLab.study.account.dto;

import com.sparkLab.study.account.constant.ActiveLevel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeActiveLevelUpdateRequest {

    @NotNull(message = "activeLevel은 필수입니다")
    private ActiveLevel activeLevel;
}
