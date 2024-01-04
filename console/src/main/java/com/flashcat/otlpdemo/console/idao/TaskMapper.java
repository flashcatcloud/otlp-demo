package com.flashcat.otlpdemo.console.idao;

import com.flashcat.otlpdemo.console.model.Task;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Mapper
public interface TaskMapper {

    public List<Task> listAll() throws DataAccessException;

    public long create(Task task) throws DataAccessException;

    public void updateStatus(Task task) throws DataAccessException;

}
