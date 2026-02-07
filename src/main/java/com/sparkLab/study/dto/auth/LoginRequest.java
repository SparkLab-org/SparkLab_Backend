package com.sparkLab.study.dto.auth;

import jakarta.validation.constraints.NotBlank;
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
public class LoginRequest {

    @NotBlank(message = "loginId는 필수입니다")
    private String loginId;

    @NotBlank(message = "loginPw는 필수입니다")
    private String loginPw;
}
