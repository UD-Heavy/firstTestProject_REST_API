package ru.spring.API.FirstTestProject.Validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.services.TaskService;

@Component
public class TaskExistValidator implements Validator {
    private final TaskService taskService;

    @Autowired
    public TaskExistValidator(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Task.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Task task = (Task) target;

        if (taskService.findByTitle(task.getTitle()).isEmpty())
            errors.rejectValue("title", "409", "Task with this title not exists");
    }
}
