package com.sparkLab.study.common.exception;

public class PlannerFixedTodoException extends RuntimeException {

    public PlannerFixedTodoException() {
        super("멘토가 등록한 할일은 수정하거나 삭제할 수 없습니다.");
    }

    public PlannerFixedTodoException(String message) {
        super(message);
    }
}
