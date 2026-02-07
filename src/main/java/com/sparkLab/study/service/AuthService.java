package com.sparkLab.study.service;

import com.sparkLab.study.constant.AccountStatus;
import com.sparkLab.study.dto.auth.LoginRequest;
import com.sparkLab.study.dto.auth.LoginResponse;
import com.sparkLab.study.entity.Account;
import com.sparkLab.study.entity.Mentee;
import com.sparkLab.study.entity.Mentor;
import com.sparkLab.study.repository.AccountRepository;
import com.sparkLab.study.repository.MenteeRepository;
import com.sparkLab.study.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!account.getLoginPw().equals(request.getLoginPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }
        Long mentorId = resolveMentorId(account.getAccountId());
        Long menteeId = resolveMenteeId(account.getAccountId());
        return LoginResponse.builder()
                .accountId(account.getAccountId())
                .role(account.getRole())
                .accountStatus(account.getAccountStatus())
                .tokenVersion(account.getTokenVersion())
                .mentorId(mentorId)
                .menteeId(menteeId)
                .build();
    }

    private Long resolveMentorId(Long accountId) {
        return mentorRepository.findByAccount_AccountId(accountId)
                .map(Mentor::getMentorId)
                .orElse(null);
    }

    private Long resolveMenteeId(Long accountId) {
        return menteeRepository.findByAccount_AccountId(accountId)
                .map(Mentee::getMenteeId)
                .orElse(null);
    }
}
