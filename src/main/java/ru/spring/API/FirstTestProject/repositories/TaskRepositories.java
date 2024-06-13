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

    Optional<Task> findByTitle(String title);

    List<Task> findByCompleted(Boolean completed, Sort sort);

    List<Task> findByCompletedAndDueDateBetween(Boolean completed, LocalDate nowDate, LocalDate rangeDate, Sort sort);

    List<Task> findByDueDateBetween(LocalDate nowDate, LocalDate rangeDate, Sort sort);

     void deleteTaskByTitle(String title);
}
