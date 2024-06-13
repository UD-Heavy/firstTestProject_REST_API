//package ru.spring.API.FirstTestProject.controllers;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.MessageSource;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import ru.spring.API.FirstTestProject.repositories.TaskRepositories;
//import ru.spring.API.FirstTestProject.services.TaskService;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class TaskControllerTest {
//
//    @Mock
//    TaskRepositories taskRepositories;
//
//    @Mock
//    MessageSource messageSource;
//
//    @InjectMocks
//    TaskController controller;
//
//    @Test
//    void getTasks_ReturnsValidResponseEntity(){
//        // given
//        var tasks = taskRepositories.findAll(Sort.by("dueDate"));
//        Mockito.doReturn(tasks).when(this.taskRepositories.findAll());
//
//        // when
//        var responseEntity = this.controller.getTasks(null, null);
//
//        // then
//        assertNotNull(responseEntity);
//        assertEquals(tasks, responseEntity.toArray());
//    }
//}