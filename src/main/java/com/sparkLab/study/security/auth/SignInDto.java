package com.sparkLab.study.security.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignInDto {

    @Getter
    @NoArgsConstructor
    public static class Req {
        private String signInId;
        private String signInPw;
    }

    @Getter
    @Builder
    public static class Res {
        private String accessToken;
    }
}
