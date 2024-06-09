package ru.spring.API.FirstTestProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public List<Task> getTask(){
      return taskService.findAll();
    }

    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello world!";
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable("id") int id) {
        return taskService.findOne(id);
    }
}
