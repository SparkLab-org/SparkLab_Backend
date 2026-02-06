package com.sparkLab.study.security.auth.controller;

import com.sparkLab.study.security.auth.dto.AccessToken;
import com.sparkLab.study.security.auth.dto.SignInReq;
import com.sparkLab.study.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signin")
    public ResponseEntity<AccessToken> signIn(@RequestBody SignInReq signInReq) {

        AccessToken accessToken = authService.signIn(signInReq);
        return ResponseEntity.ok(accessToken);
    }

}
