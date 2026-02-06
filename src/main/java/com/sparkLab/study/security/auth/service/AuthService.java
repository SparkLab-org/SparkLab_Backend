package com.sparkLab.study.security.auth.service;

import com.sparkLab.study.exception.BusinessException;
import com.sparkLab.study.security.auth.Account;
import com.sparkLab.study.security.auth.AccountRepository;
import com.sparkLab.study.security.auth.dto.AccessToken;
import com.sparkLab.study.security.auth.dto.SignInReq;
import com.sparkLab.study.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final TokenService tokenService;

    public AccessToken signIn(SignInReq signInReq) {
        Account account = accountRepository.findById(signInReq.accountId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Account를 찾을 수 없습니다."
                        )
                );

        String accessToken = tokenService.issueToken(account.getAccountId(), account.getRole());
        return new AccessToken(accessToken);
    }
}
