package com.sparkLab.study.security.auth.exception;

import com.sparkLab.study.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(String accountId) {
        super("Account를 찾을 수 없습니다. accountId=" + accountId);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}
