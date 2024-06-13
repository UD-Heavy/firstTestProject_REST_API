package ru.spring.API.FirstTestProject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class TaskCompleteUpdateDTO { // DTO для запроса обновления задачи
    @NotEmpty(message = "Title should be not empty")
    @Size(max = 100, message = "Title should be shorter than 100 characters")
    private String title;

    private boolean completed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
