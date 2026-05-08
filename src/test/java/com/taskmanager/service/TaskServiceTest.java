package com.taskmanager.service;

import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setPriority(Task.Priority.MEDIUM);
        task.setStatus(Task.Status.TODO);
    }

    @Test
    void createTask_savesAndReturnsTask() {
        when(repository.save(task)).thenReturn(task);
        Task result = taskService.createTask(task);
        assertEquals(task, result);
        verify(repository).save(task);
    }

    @Test
    void getAllTasks_returnsAllTasks() {
        when(repository.findAll()).thenReturn(List.of(task));
        List<Task> result = taskService.getAllTasks();
        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void getTaskById_returnsTask_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        Task result = taskService.getTaskById(1L);
        assertEquals(task, result);
    }

    @Test
    void getTaskById_throwsException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void updateTask_updatesAndReturnsTask() {
        Task updated = new Task();
        updated.setTitle("Updated Title");
        updated.setPriority(Task.Priority.HIGH);
        updated.setStatus(Task.Status.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Task result = taskService.updateTask(1L, updated);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Task.Priority.HIGH, result.getPriority());
    }

    @Test
    void deleteTask_deletesTask_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        taskService.deleteTask(1L);
        verify(repository).delete(task);
    }
}