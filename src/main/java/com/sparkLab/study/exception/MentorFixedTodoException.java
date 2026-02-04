package com.sparkLab.study.exception;

/**
 * 멘토가 고정한 할일은 멘티가 수정/삭제할 수 없을 때 사용.
 */
public class MentorFixedTodoException extends RuntimeException {

    public MentorFixedTodoException() {
        super("멘토가 등록한 할일은 수정하거나 삭제할 수 없습니다.");
    }

    public MentorFixedTodoException(String message) {
        super(message);
    }
}
