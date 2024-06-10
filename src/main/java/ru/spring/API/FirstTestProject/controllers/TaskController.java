package ru.spring.API.FirstTestProject.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.spring.API.FirstTestProject.dto.TaskDTO;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.services.TaskService;
import ru.spring.API.FirstTestProject.utils.TaskErrorResponse;
import ru.spring.API.FirstTestProject.utils.TaskNotCreatedException;
import ru.spring.API.FirstTestProject.utils.TaskNotFoundException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ModelMapper modelMapper;
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<TaskDTO> getTask() {
        return taskService.findAll().stream().map(this::convertToTaskDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable("id") int id) {
        return convertToTaskDTO(taskService.findOne(id));
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createTask(@RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" — ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new TaskNotCreatedException(errorMsg.toString());
        }
        taskService.save(convertToTask(taskDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<TaskErrorResponse> handleException(TaskNotFoundException e) {
        TaskErrorResponse response = new TaskErrorResponse(
                "Task with this id wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<TaskErrorResponse> handleException(TaskNotCreatedException e) {
        TaskErrorResponse response = new TaskErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    private Task convertToTask(TaskDTO taskDTO){
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskDTO convertToTaskDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
