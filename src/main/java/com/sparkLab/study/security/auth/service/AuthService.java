package com.sparkLab.study.security.auth.service;

import com.sparkLab.study.exception.BusinessException;
import com.sparkLab.study.security.auth.entity.Account;
import com.sparkLab.study.security.auth.repository.AccountRepository;
import com.sparkLab.study.security.auth.dto.AccessToken;
import com.sparkLab.study.security.auth.dto.SignInReq;
import com.sparkLab.study.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LogManager.getLogger(AuthService.class);
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
        log.info(accessToken);
        return new AccessToken(accessToken);
    }

}
