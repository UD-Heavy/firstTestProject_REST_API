package ru.spring.API.FirstTestProject.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.spring.API.FirstTestProject.validators.TaskExistValidator;
import ru.spring.API.FirstTestProject.validators.TaskValidator;
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
public class TaskController { // Этот класс является Spring REST-контроллером,
    // который обрабатывает API-запросы для управления задачами.

    // Это зависимости, необходимые контроллеру для выполнения своих задач.
    private final TaskValidator taskValidator;
    private final TaskExistValidator taskExistValidator;
    private final ModelMapper modelMapper;
    private final TaskService taskService;

    // Это конструктор, используемый для внедрения зависимостей в контроллер.
    @Autowired
    public TaskController(TaskService taskService, ModelMapper modelMapper, TaskValidator taskValidator, TaskExistValidator taskExistValidator) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
        this.taskValidator = taskValidator;
        this.taskExistValidator = taskExistValidator;
    }

    // Этот метод обрабатывает GET-запрос для получения всех задач.
    // Он поддерживает необязательные query-параметры "completed" и "date_range" для фильтрации задач.
    @GetMapping()
    public ResponseEntity<List<TaskDTO>> getTasks(@RequestParam(value = "completed", required = false) Boolean completed,
                                                  @RequestParam(value = "date_range", required = false, defaultValue = "") String dateRange) {
        // Получаем все задачи на основе предоставленных фильтров и преобразуем их в DTO.
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.findAll(completed, dateRange).stream().map(this::convertTaskToTaskDTO).collect(Collectors.toList()));
    }

    // Этот метод обрабатывает GET-запрос для получения единственной задачи по ее ID.
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") int id) {
        // Получаем задачу по ее ID и преобразуем ее в DTO.
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(convertTaskToTaskDTO(taskService.findOne(id)));
    }

    // Этот метод обрабатывает POST-запрос для создания новой задачи.
    // Он валидирует входные данные с помощью TaskValidator.
    @PostMapping()
    public ResponseEntity<HttpStatus> createTask(@RequestBody @Valid TaskDTO taskDTO,
                                                 BindingResult bindingResult) {
        // Преобразуем DTO в сущность Task и валидируем ее.
        Task task = convertTaskDTOToTask(taskDTO);

        taskValidator.validate(task, bindingResult);

        if (bindingResult.hasErrors()) { // Если есть ошибки валидации, возвращаем их клиенту.
            returnErrorsToClient(bindingResult);
        }
        // Сохраняем задачу и возвращаем успешный ответ.
        taskService.save(task);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Этот метод обрабатывает PATCH-запрос для обновления существующей задачи.
    @PatchMapping()
    public ResponseEntity<TaskUpdatedDTO> updateTask(@RequestParam(value = "title") String title,
                                                     @RequestBody @Valid TaskUpdatedDTO taskDTO) {

        // Обновляем задачу и преобразуем обновленную задачу в DTO.
        taskDTO = convertTaskToTAskUpdatedDTO(taskService.update(taskDTO, title));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskDTO);
    }

    // Этот метод обрабатывает PATCH-запрос для обновления статуса завершенности задачи.
    // Он валидирует входные данные с помощью TaskExistValidator.
    @PatchMapping("/completed")
    public ResponseEntity<TaskCompleteUpdateDTO> updateCompleteStatus(@RequestBody @Valid TaskCompleteUpdateDTO taskCompleteUpdateDTO,
                                                                      BindingResult bindingResult) {
        // Преобразуем DTO в сущность Task и валидируем ее.
        Task task = convertTaskCompleteUpdateToTask(taskCompleteUpdateDTO);

        taskExistValidator.validate(task, bindingResult);

        if (bindingResult.hasErrors()) { // Если есть ошибки валидации, возвращаем их клиенту.
            returnErrorsToClient(bindingResult);
        }

        // Обновляем статус завершенности задачи и преобразуем обновленную задачу в DTO.
        taskCompleteUpdateDTO = convertTaskToTaskCompleteUpdateDTO(taskService.updateComplete(task));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskCompleteUpdateDTO);
    }

    // Этот метод обрабатывает DELETE-запрос для удаления задачи по ее названию.
    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteTask(@RequestParam(value = "title") String title) {
        // Удаляем задачу по ее названию и возвращаем успешный ответ.
        taskService.delete(title);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Методы, отлавливающие выбрасывающие ошибки.
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

    @ExceptionHandler
    private ResponseEntity<TaskErrorResponse> handleException(RuntimeException e) {
        TaskErrorResponse response = new TaskErrorResponse(
                "Invalid JSON parameter value",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // Методы для конвертирования DTO объектов в задачи и обратно.
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
