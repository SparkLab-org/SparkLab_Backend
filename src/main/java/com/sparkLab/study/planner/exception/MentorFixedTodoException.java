package com.sparkLab.study.planner.exception;

import com.sparkLab.study.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 멘토가 고정한 할일은 멘티가 수정/삭제할 수 없을 때 사용.
 */
public class MentorFixedTodoException extends BusinessException {

    public MentorFixedTodoException() {
        super( "멘토가 등록한 할일은 수정하거나 삭제할 수 없습니다.");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.FORBIDDEN;
    }
}
