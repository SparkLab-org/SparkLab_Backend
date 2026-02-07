package com.sparkLab.study.dto.auth;

import com.sparkLab.study.constant.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long accountId;
    private String role;
    private AccountStatus accountStatus;
    private Long tokenVersion;
    private Long mentorId;
    private Long menteeId;
}
