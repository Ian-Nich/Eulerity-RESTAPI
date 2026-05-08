package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taskmanager.dto.SuggestRequest;
import com.taskmanager.dto.SuggestResponse;
import com.taskmanager.exception.GlobalExceptionHandler;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.model.Task;
import com.taskmanager.service.AiSuggestionService;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerIntegrationTest {

    private MockMvc mockMvc;
    private TaskService taskService;
    private AiSuggestionService aiSuggestionService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        aiSuggestionService = mock(AiSuggestionService.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TaskController(taskService, aiSuggestionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createTask_returns201() throws Exception {
        Task task = new Task();
        task.setTitle("Integration Test Task");
        task.setPriority(Task.Priority.LOW);
        task.setStatus(Task.Status.TODO);

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Integration Test Task");
        saved.setPriority(Task.Priority.LOW);
        saved.setStatus(Task.Status.TODO);

        when(taskService.createTask(any(Task.class))).thenReturn(saved);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"));
    }

    @Test
    void getAllTasks_returns200() throws Exception {
        when(taskService.getAllTasks()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaskById_returns404_whenNotFound() throws Exception {
        when(taskService.getTaskById(99999L)).thenThrow(new TaskNotFoundException(99999L));

        mockMvc.perform(get("/tasks/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_returnsUpdatedTask() throws Exception {
        Task task = new Task();
        task.setTitle("Updated");
        task.setPriority(Task.Priority.LOW);
        task.setStatus(Task.Status.IN_PROGRESS);

        Task returned = new Task();
        returned.setId(1L);
        returned.setTitle("Updated");
        returned.setPriority(Task.Priority.LOW);
        returned.setStatus(Task.Status.IN_PROGRESS);

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(returned);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void suggestTask_returnsSuggestion_withMockedAi() throws Exception {
        SuggestResponse mocked = new SuggestResponse(
                "HIGH",
                LocalDate.now().plusWeeks(1).toString(),
                "TODO",
                "This looks urgent based on the title."
        );
        when(aiSuggestionService.suggest(any(SuggestRequest.class))).thenReturn(mocked);

        SuggestRequest req = new SuggestRequest();
        req.setTitle("Submit quarterly report");

        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedPriority").value("HIGH"))
                .andExpect(jsonPath("$.explanation").value("This looks urgent based on the title."));
    }
}