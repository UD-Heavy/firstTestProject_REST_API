package ru.spring.API.FirstTestProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.repositories.TaskRepositories;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepositories taskRepositories;

    @Autowired
    public TaskService(TaskRepositories taskRepositories) {
        this.taskRepositories = taskRepositories;
    }

    public List<Task> findAll (){
        return taskRepositories.findAll();
    }

    public Task findOne(int id) {
        Optional<Task> foundTask = taskRepositories.findById(id);
        return foundTask.orElse(null);
    }
}
