package com.sparkLab.study.security.auth.controller;

import com.sparkLab.study.security.auth.dto.AccessToken;
import com.sparkLab.study.security.auth.dto.SignInReq;
import com.sparkLab.study.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AccessToken> signIn(@RequestBody SignInReq signInReq) {

        AccessToken accessToken = authService.signIn(signInReq);
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/me")
    public Map<String, Object> getMe(@AuthenticationPrincipal Jwt jwt) {
        // Jwt에서 claim 추출

        log.info(jwt.getHeaders().toString());
        String accountId = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("roles");

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", accountId);
        response.put("roles", roles);

        return response;
    }
}
