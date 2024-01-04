package com.flashcat.otlpdemo.console.dao;

import com.flashcat.otlpdemo.console.idao.TaskMapper;
import com.flashcat.otlpdemo.console.model.Task;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskDao {

    @Autowired
    private TaskMapper taskMapper;

    public PageInfo<Task> getTasksByPage(int pageNum, int pageSize) throws DataAccessException {
        PageHelper.startPage(pageNum, pageSize);
        List<Task> tasks = taskMapper.listAll();
        return new PageInfo<>(tasks);
    }

    public long createTask(Task task) throws DataAccessException {
        taskMapper.create(task);
        return task.getId();
    }

    public void updateTaskStatus(Task task) throws DataAccessException {
        taskMapper.updateStatus(task);
    }
}
