package com.sparkLab.study.security.auth.service;

import com.sparkLab.study.security.auth.constant.SignStatus;
import com.sparkLab.study.security.auth.entity.Account;
import com.sparkLab.study.security.auth.exception.AccountNotFoundException;
import com.sparkLab.study.security.auth.repository.AccountRepository;
import com.sparkLab.study.security.auth.dto.AccessToken;
import com.sparkLab.study.security.auth.dto.SignInReq;
import com.sparkLab.study.security.jwt.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final TokenService tokenService;

    public AccessToken signIn(SignInReq signInReq) {
        Account account = accountRepository.findById(signInReq.accountId())
                .orElseThrow(() -> new AccountNotFoundException(signInReq.accountId())
                );

        String accessToken = tokenService.issueToken(account.getAccountId(), account.getRole());
        log.info(accessToken);
        return new AccessToken(accessToken);
    }

    @Transactional
    public void signOut(String accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.changeSignStatus(SignStatus.SIGN_OUT);
        log.info("Account {} signed out", accountId);
    }
}
