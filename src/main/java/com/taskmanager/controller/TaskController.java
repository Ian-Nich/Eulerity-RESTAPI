package com.taskmanager.controller;

import com.taskmanager.dto.SuggestRequest;
import com.taskmanager.dto.SuggestResponse;
import com.taskmanager.service.AiSuggestionService;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    private final AiSuggestionService aiSuggestionService;

    public TaskController(TaskService taskService, AiSuggestionService aiSuggestionService) {
    this.taskService = taskService;
    this.aiSuggestionService = aiSuggestionService;
}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/suggest")
    public SuggestResponse suggestTask(@Valid @RequestBody SuggestRequest request) {
    return aiSuggestionService.suggest(request);
    }       
}