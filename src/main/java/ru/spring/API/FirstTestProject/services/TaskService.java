package ru.spring.API.FirstTestProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.API.FirstTestProject.models.Task;
import ru.spring.API.FirstTestProject.repositories.TaskRepositories;
import ru.spring.API.FirstTestProject.utils.E_DATA_PERIODS;
import ru.spring.API.FirstTestProject.utils.IncorrectDateException;
import ru.spring.API.FirstTestProject.utils.TaskNotCreatedException;
import ru.spring.API.FirstTestProject.utils.TaskNotFoundException;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepositories taskRepositories;

    @Autowired
    public TaskService(TaskRepositories taskRepositories) {
        this.taskRepositories = taskRepositories;
    }

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


    public Task findOne(int id) {
        Optional<Task> foundTask = taskRepositories.findById(id);
        return foundTask.orElseThrow(TaskNotFoundException::new);
    }

    public Optional<Task> findByTitle(String title) {
        return taskRepositories.findByTitle(title);
    }

    @Transactional
    public void save(Task task) {
        enrichTask(task);
        taskRepositories.save(task);
    }

    @Transactional
    public Task update(Task task, String title) {
        Task foundTask = taskRepositories.findByTitle(title).orElseThrow(TaskNotFoundException::new);
        if (!Objects.equals(title, task.getTitle())
                && task.getTitle() != null
                && taskRepositories.findByTitle(task.getTitle()).isPresent())
            throw new TaskNotCreatedException("Task with this title already exists");
        compareAndUpdate(task, foundTask);
        taskRepositories.save(foundTask);
        return foundTask;
    }

    @Transactional
    public Task updateComplete(Task task) {
        Task updatedTask = taskRepositories.findByTitle(task.getTitle()).orElseThrow(TaskNotFoundException::new);
        updatedTask.setCompleted(task.isCompleted());
        taskRepositories.save(updatedTask);
        return updatedTask;
    }

    @Transactional
    public void delete(String title) {
        Optional<Task> foundTask = taskRepositories.findByTitle(title);
        if (foundTask.isEmpty()) throw new TaskNotFoundException();
        taskRepositories.deleteById(foundTask.get().getId());
    }

    private void enrichTask(Task task) {
        task.setCompleted(false);
    }

    private void compareAndUpdate(Task newTask, Task oldTask) {
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
        if (newTask.isCompleted() != oldTask.isCompleted()) {
            oldTask.setCompleted(newTask.isCompleted());
        }
    }

}
