package ru.spring.API.FirstTestProject.utils;

public class TaskNotCreatedException extends RuntimeException {
    public TaskNotCreatedException(String msg) {
        super(msg);
    }
}
