package com.sparkLab.study.exception;

public class TaskResourceNotFoundException extends RuntimeException {

    public TaskResourceNotFoundException(String message) {
        super(message);
    }
}
