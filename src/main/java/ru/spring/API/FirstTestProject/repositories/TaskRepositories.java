package ru.spring.API.FirstTestProject.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spring.API.FirstTestProject.models.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepositories extends JpaRepository<Task, Integer> {
    // Этот интерфейс наследует JpaRepository, который предоставляет множество стандартных
    // методов для работы с сущностью Task.
    // Аннотация @Repository указывает, что этот интерфейс является репозиторием,
    // отвечающим за взаимодействие с базой данных.

    // Этот метод позволяет найти задачу (Task) по ее названию (title).
    // Он возвращает Optional<Task>, что означает, что метод может вернуть либо найденную задачу,
    // либо null, если задача с указанным названием не найдена.
    Optional<Task> findByTitle(String title);

    // Этот метод позволяет найти все задачи (Task) по их состоянию (completed).
    // Он возвращает список задач (List<Task>),
    // отсортированный в соответствии с переданным объектом Sort.
    List<Task> findByCompleted(Boolean completed, Sort sort);

    // Этот метод позволяет найти все задачи (Task) по их состоянию (completed)
    // и сроку выполнения (dueDateBetween).
    // Он возвращает список задач (List<Task>), отсортированный
    // в соответствии с переданным объектом Sort.
    // Задачи будут найдены, если их состояние соответствует
    // переданному значению completed, и срок выполнения находится
    // в диапазоне между nowDate и rangeDate.
    List<Task> findByCompletedAndDueDateBetween(Boolean completed, LocalDate nowDate, LocalDate rangeDate, Sort sort);

    // Этот метод позволяет найти все задачи (Task) по их
    // сроку выполнения (dueDateBetween).
    // Он возвращает список задач (List<Task>), отсортированный
    // в соответствии с переданным объектом Sort.
    // Задачи будут найдены, если их срок выполнения находится
    // в диапазоне между nowDate и rangeDate.
    List<Task> findByDueDateBetween(LocalDate nowDate, LocalDate rangeDate, Sort sort);

    // Этот метод позволяет удалить задачу (Task) по ее названию (title).
    void deleteTaskByTitle(String title);
}
