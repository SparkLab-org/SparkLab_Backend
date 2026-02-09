package com.sparkLab.study.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotOwnerException extends BusinessException {

    private final Long resourceId;
    private final String resourceType;

    public NotOwnerException(String resourceType, Long resourceId) {
        super("%s 리소스의 소유자가 아닙니다. id=%d".formatted(resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.FORBIDDEN; // 403
    }
}
