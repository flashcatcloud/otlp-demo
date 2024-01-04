CREATE TABLE task
(
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    trace_id       VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'trace id',
    parent_span_id VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'parent span id',
    task_name      VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'task name',
    task_status    VARCHAR(16) NOT NULL DEFAULT 'init' COMMENT 'task status'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='task';
