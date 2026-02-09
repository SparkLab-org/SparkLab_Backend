package com.sparkLab.study.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ParentResourceNotFoundException extends BusinessException {

    private final String resourceType; // 리소스 타입
    private final Long resourceId;     // 리소스 아이디

    public ParentResourceNotFoundException(String resourceType, Long resourceId) {
        super("Parent Resource not found: type=%s, id=%d".formatted(resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}
