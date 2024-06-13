package ru.spring.API.FirstTestProject.utils;

public class TaskErrorResponse { // класс для конструктора возврата кастомной ошибки
    private String message;
    private long timestamp;

    public TaskErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
