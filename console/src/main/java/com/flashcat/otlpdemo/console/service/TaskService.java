package com.flashcat.otlpdemo.console.service;

import com.flashcat.otlpdemo.console.dao.TaskDao;
import com.flashcat.otlpdemo.console.model.Task;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    public List<com.flashcat.otlpdemo.console.model.Task> getTasksByPage(int pageNum, int pageSize) throws DataAccessException {
        return taskDao.getTasksByPage(pageNum, pageSize).getList();
    }

    @WithSpan(value = "createTaskService") // 自定义 Span 名称
    public long createTask(@SpanAttribute("parameter")/*将参数放到attribute中*/ Task task) throws DataAccessException {
        Span span = Span.current(); // 手动给当前Span添加数据
        span.setAttribute("test_attribute_post_key", "test_attribute_post_value");
        return taskDao.createTask(task);
    }

    @WithSpan(value = "updateTaskStatusService") // 自定义 Span 名称
    public void updateTaskStatus(@SpanAttribute("parameter")/*将参数放到attribute中*/ Task task) throws DataAccessException {
        Span span = Span.current(); // 手动给当前Span添加数据
        span.setAttribute("test_attribute_update_key", "test_attribute_update_value");
        taskDao.updateTaskStatus(task);
    }
}
