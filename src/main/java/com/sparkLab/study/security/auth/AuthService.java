package com.sparkLab.study.security.auth;

import com.sparkLab.study.entity.Account;
import com.sparkLab.study.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignInDto.Res signIn(SignInDto.Req signInReq) {

        Account account = accountRepository.findById(signInReq.getSignInId())
                .orElseThrow(() -> new RuntimeException("ACCOUNT_NOT_FOUND"));

        if (!passwordEncoder.matches(signInReq.getSignInPw(), account.getLoginPw())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        String role = account.getRole();

        UserDetails userDetails = User.builder()
                .username(account.getAccountId())
                .password(account.getLoginPw())
                .authorities(role)
                .build();

        String accessToken = jwtUtil.issueAccessToken(userDetails);

        return SignInDto.Res.builder()
                .accessToken(accessToken)
                .role(role)
                .build();
    }



}
