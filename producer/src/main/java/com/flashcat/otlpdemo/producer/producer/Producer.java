package com.flashcat.otlpdemo.producer.producer;

import com.flashcat.otlpdemo.producer.service.http.TaskHTTPService;
import com.flashcat.otlpdemo.producer.service.kafka.TaskKafkaService;
import com.flashcat.otlpdemo.producer.model.Task;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.opentelemetry.context.Scope;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Producer {
    @Value("${otlpdemo.producer.interval}")
    private long interval;

    @Autowired
    private TaskKafkaService tk;
    @Autowired
    private TaskHTTPService th;

    private Tracer tracer = GlobalOpenTelemetry.get().getTracer(Producer.class.getName());

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    public void doProducer() {
        while (true) {
            Span span = tracer.spanBuilder("doProducer").startSpan();
            try (Scope scope = span.makeCurrent()) {
                process();
            } catch (Exception e) {
                span.setStatus(StatusCode.ERROR);
                span.recordException(e);
            } finally {
                span.end();
            }
            try {
                Thread.sleep(this.interval * 1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void process() {
        // 手动创建span的演示
        Span span = tracer.spanBuilder("process").startSpan();

        // 需要调用makeCurrent创建当前的span内容
        try (Scope scope = span.makeCurrent()) {
            String name = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
            // logback 演示
            logger.info("start producer task, name is " + name);
            Task task = new Task(name);
            task.setTraceId(Span.current().getSpanContext().getTraceId());

            th.setTask(task);
            long id;
            // 手动使用span的演示
            try {
                id = th.createTask();
            } catch (Exception e) {
                span.setStatus(StatusCode.ERROR);
                span.recordException(e);
                return;
            }

            task.setId(id);
            tk.setTask(task);
            try {
                tk.sendTask();
            } catch (Exception e) {
                span.setStatus(StatusCode.ERROR);
                span.recordException(e);
            }
            logger.info("producer task success, name is " + name);
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}
