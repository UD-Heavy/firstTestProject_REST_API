package ru.spring.API.FirstTestProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spring.API.FirstTestProject.models.Task;

@Repository
public interface TaskRepositories extends JpaRepository<Task, Integer> {

}
