package ru.spring.API.FirstTestProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.API.FirstTestProject.dto.TaskUpdatedDTO;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.repositories.TaskRepositories;
import ru.spring.API.FirstTestProject.utils.E_DATA_PERIODS;
import ru.spring.API.FirstTestProject.utils.IncorrectDateException;
import ru.spring.API.FirstTestProject.utils.TaskNotCreatedException;
import ru.spring.API.FirstTestProject.utils.TaskNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    // Класс помечен аннотацией @Service, что означает, что он является компонентом службы в Spring.
    // Аннотация @Transactional(readOnly = true) указывает, что все методы этого класса являются транзакционными
    // и только для чтения, за исключением тех, которые помечены как транзакционные для записи.
    // Конструктор этого класса принимает TaskRepositories в качестве зависимости.
    private final TaskRepositories taskRepositories;

    @Autowired
    public TaskService(TaskRepositories taskRepositories) {
        this.taskRepositories = taskRepositories;
    }

    // Метод для поиска всех задач с фильтрацией по статусу завершенности и диапазону дат
    public List<Task> findAll(Boolean sortByCompleted, String dateRange) throws IncorrectDateException {
        if (!dateRange.isEmpty()) {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(E_DATA_PERIODS.getValueFromString(dateRange.toUpperCase()));
            if (sortByCompleted != null)
                return taskRepositories.findByCompletedAndDueDateBetween(sortByCompleted, startDate, endDate,
                        Sort.by("dueDate"));
            else
                return taskRepositories.findByDueDateBetween(startDate, endDate, Sort.by("dueDate"));

        } else if (sortByCompleted != null)
            return taskRepositories.findByCompleted(sortByCompleted, Sort.by("dueDate"));
        else
            return taskRepositories.findAll(Sort.by("dueDate"));
    }


    // Метод для поиска одной задачи по id
    public Task findOne(int id) {
        Optional<Task> foundTask = taskRepositories.findById(id);
        return foundTask.orElseThrow(TaskNotFoundException::new);
    }

    // Метод для поиска одной задачи по title
    public Optional<Task> findByTitle(String title) {
        return taskRepositories.findByTitle(title);
    }

    // Метод для сохранения новой задачи
    @Transactional
    public void save(Task task) {
        enrichTask(task);
        taskRepositories.save(task);
    }

    // Метод для обновления существующей задачи
    @Transactional
    public Task update(TaskUpdatedDTO task, String title) throws RuntimeException {
        Task foundTask = taskRepositories.findByTitle(title).orElseThrow(TaskNotFoundException::new);
        if (!Objects.equals(title, task.getTitle())
                && task.getTitle() != null
                && taskRepositories.findByTitle(task.getTitle()).isPresent())
            throw new TaskNotCreatedException("Task with this title already exists");
        compareAndUpdate(task, foundTask);
        taskRepositories.save(foundTask);
        return foundTask;
    }

    // Метод для обновления статуса завершенности задачи
    @Transactional
    public Task updateComplete(Task task) {
        Task updatedTask = taskRepositories.findByTitle(task.getTitle()).orElseThrow(TaskNotFoundException::new);
        updatedTask.setCompleted(task.isCompleted());
        taskRepositories.save(updatedTask);
        return updatedTask;
    }

    // Метод для удаления задачи по названию
    @Transactional
    public void delete(String title) {
        Optional<Task> foundTask = taskRepositories.findByTitle(title);
        if (foundTask.isEmpty()) throw new TaskNotFoundException();
        taskRepositories.deleteById(foundTask.get().getId());
    }

    // Метод для обогащения задачи дополнительной информацией
    private void enrichTask(Task task) {
        task.setCompleted(false);
    }

    // Метод сравнения задач и обновления полей
    private void compareAndUpdate(TaskUpdatedDTO newTask, Task oldTask) throws RuntimeException {
        // Сравнение и обновление title
        if (!Objects.equals(newTask.getTitle(), oldTask.getTitle()) && newTask.getTitle() != null) {
            oldTask.setTitle(newTask.getTitle());
        }

        // Сравнение и обновление description
        if (!Objects.equals(newTask.getDescription(), oldTask.getDescription()) && newTask.getDescription() != null) {
            oldTask.setDescription(newTask.getDescription());
        }

        // Сравнение и обновление dueDate
        if (!Objects.equals(newTask.getDueDate(), oldTask.getDueDate()) && newTask.getDueDate() != null) {
            oldTask.setDueDate(newTask.getDueDate());
        }

        // Сравнение и обновление completed
        if (newTask.isCompleted() != null && (newTask.isCompleted() != oldTask.isCompleted())) {
            oldTask.setCompleted(newTask.isCompleted());
        }
    }

}
