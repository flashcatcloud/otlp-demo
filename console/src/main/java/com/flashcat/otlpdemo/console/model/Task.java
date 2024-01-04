package com.flashcat.otlpdemo.console.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
    private long id;
    private String traceID;
    private String parentSpanID;
    private String taskName;
    private String taskStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getParentSpanID() {
        return parentSpanID;
    }

    public void setParentSpanID(String parentSpanID) {
        this.parentSpanID = parentSpanID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}

