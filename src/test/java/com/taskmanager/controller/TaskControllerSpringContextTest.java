package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.AiSuggestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerSpringContextTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private AiSuggestionService aiSuggestionService;

    private RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }

    private Task buildTask(String title) {
        Task t = new Task();
        t.setTitle(title);
        t.setPriority(Task.Priority.MEDIUM);
        t.setStatus(Task.Status.TODO);
        return t;
    }

    @Test
    void springContextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void createTask_returns201_withSpringContext() {
        ResponseEntity<Task> res = client().post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildTask("Spring Context Task"))
                .retrieve()
                .toEntity(Task.class);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("Spring Context Task", res.getBody().getTitle());
    }

    @Test
    void getAllTasks_returns200_withSpringContext() {
        ResponseEntity<Task[]> res = client().get()
                .uri("/tasks")
                .retrieve()
                .toEntity(Task[].class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    void getTaskById_returns404_withSpringContext() {
        try {
            client().get()
                    .uri("/tasks/99999")
                    .retrieve()
                    .toEntity(String.class);
            fail("Expected 404 exception");
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }
    }

    @Test
    void updateTask_returnsUpdatedTask_withSpringContext() {
        Task created = client().post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildTask("Original"))
                .retrieve()
                .body(Task.class);

        assertNotNull(created);
        Long id = created.getId();

        Task updated = buildTask("Updated");
        updated.setStatus(Task.Status.IN_PROGRESS);

        ResponseEntity<Task> res = client().put()
                .uri("/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updated)
                .retrieve()
                .toEntity(Task.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("Updated", res.getBody().getTitle());
    }

    @Test
    void deleteTask_returns204_withSpringContext() {
        Task created = client().post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildTask("To Delete"))
                .retrieve()
                .body(Task.class);

        assertNotNull(created);
        Long id = created.getId();

        ResponseEntity<Void> res = client().delete()
                .uri("/tasks/" + id)
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}