package ru.spring.API.FirstTestProject.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.spring.API.FirstTestProject.Validators.TaskExistValidator;
import ru.spring.API.FirstTestProject.Validators.TaskValidator;
import ru.spring.API.FirstTestProject.dto.TaskCompleteUpdateDTO;
import ru.spring.API.FirstTestProject.dto.TaskDTO;
import ru.spring.API.FirstTestProject.dto.TaskUpdatedDTO;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.services.TaskService;
import ru.spring.API.FirstTestProject.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.spring.API.FirstTestProject.utils.ErrorsUtil.returnErrorsToClient;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskValidator taskValidator;
    private final TaskExistValidator taskExistValidator;
    private final ModelMapper modelMapper;
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService, ModelMapper modelMapper, TaskValidator taskValidator, TaskExistValidator taskExistValidator) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
        this.taskValidator = taskValidator;
        this.taskExistValidator = taskExistValidator;
    }

    @GetMapping()
    public List<TaskDTO> getTask(@RequestParam(value = "completed", required = false) Boolean completed,
                                 @RequestParam(value = "date_range", required = false, defaultValue = "") String dateRange) {
        return taskService.findAll(completed, dateRange).stream().map(this::convertTaskToTaskDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable("id") int id) {
        return convertTaskToTaskDTO(taskService.findOne(id));
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createTask(@RequestBody @Valid TaskDTO taskDTO,
                                                 BindingResult bindingResult) {
        Task task = convertTaskDTOToTask(taskDTO);

        taskValidator.validate(task, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        taskService.save(task);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping()
    public TaskUpdatedDTO updateTask(@RequestParam(value = "title", required = true) String title,
                                     @RequestBody @Valid TaskUpdatedDTO taskDTO,
                                     BindingResult bindingResult) {

        Task task = convertTaskUpdatedDTOToTask(taskDTO);


        taskDTO = convertTaskToTAskUpdatedDTO(taskService.update(task, title));
        return taskDTO;
    }

    @PatchMapping("/complete")
    public TaskCompleteUpdateDTO updateCompleteStatus(@RequestBody @Valid TaskCompleteUpdateDTO taskCompleteUpdateDTO,
                                                      BindingResult bindingResult) {
        Task task = convertTaskCompleteUpdateToTask(taskCompleteUpdateDTO);

        taskExistValidator.validate(task, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        taskCompleteUpdateDTO = convertTaskToTaskCompleteUpdateDTO(taskService.updateComplete(task));
        return taskCompleteUpdateDTO;
    }


    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteTask(@RequestParam(value = "title", required = true) String title) {
        taskService.delete(title);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
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

    @ExceptionHandler
    private ResponseEntity<TaskErrorResponse> handleException(IncorrectDateException e) {
        TaskErrorResponse response = new TaskErrorResponse(
                "Invalid time period specified",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<TaskErrorResponse> handleConversionFailedException(MethodArgumentTypeMismatchException e) {
        TaskErrorResponse response = new TaskErrorResponse(
                "Invalid parameter value",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Task convertTaskDTOToTask(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskDTO convertTaskToTaskDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }

    private Task convertTaskCompleteUpdateToTask(TaskCompleteUpdateDTO taskCompleteUpdateDTO) {
        return modelMapper.map(taskCompleteUpdateDTO, Task.class);
    }

    private TaskCompleteUpdateDTO convertTaskToTaskCompleteUpdateDTO(Task task) {
        return modelMapper.map(task, TaskCompleteUpdateDTO.class);
    }

    private Task convertTaskUpdatedDTOToTask(TaskUpdatedDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskUpdatedDTO convertTaskToTAskUpdatedDTO(Task task) {
        return modelMapper.map(task, TaskUpdatedDTO.class);
    }
}
