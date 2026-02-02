package com.sparkLab.study.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<SignInDto.Res> signIn(@RequestBody SignInDto.Req signInReq){

        SignInDto.Res signInRes = authService.signIn(signInReq);
        return ResponseEntity.ok(signInRes);
    }

}
