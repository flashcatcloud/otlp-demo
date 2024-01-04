package com.flashcat.otlpdemo.console.controller;

import com.flashcat.otlpdemo.console.model.Task;
import com.flashcat.otlpdemo.console.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TasksController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    @ExceptionHandler(Exception.class)
    public List<Task> getTasksByPage(@RequestParam int pageNum, @RequestParam int pageSize) {
        return taskService.getTasksByPage(pageNum, pageSize);
    }

    @PostMapping("/tasks")
    @ExceptionHandler(Exception.class)
    public Map<String, Long> createTask(@RequestBody Task task) {
        long id = taskService.createTask(task);
        return Collections.singletonMap("id", id);
    }
}
