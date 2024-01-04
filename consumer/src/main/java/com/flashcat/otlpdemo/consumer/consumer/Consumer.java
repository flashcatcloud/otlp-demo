package com.flashcat.otlpdemo.consumer.consumer;

import com.flashcat.otlpdemo.consumer.model.Task;
import com.flashcat.otlpdemo.consumer.service.http.TaskHTTPService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
public class Consumer {
    @Autowired
    private org.apache.kafka.clients.consumer.Consumer<String, String> consumer;
    @Value("${otlpdemo.topic}")
    private String topic;
    @Autowired
    private TaskHTTPService th;
    private Tracer tracer = GlobalOpenTelemetry.get().getTracer(Consumer.class.getName());

    public void doConsumer() {
        try {
            consumer.subscribe(Collections.singletonList(this.topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, String> record : records) {
                    Span span = tracer.spanBuilder("doConsumerHandle").startSpan();
                    try (Scope scope = span.makeCurrent()) {
                        this.handle(record);
                    } catch (Exception e) {
                        span.setStatus(StatusCode.ERROR);
                        span.recordException(e);
                    } finally {
                        span.end();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(ConsumerRecord<String, String> record) {
        Span span = tracer.spanBuilder("consumerHandleProcess").startSpan();
        try (Scope scope = span.makeCurrent()) {
            String json = record.value();
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            Task taskObj = gson.fromJson(json, Task.class);
            taskObj.setTaskStatus("done");
            th.setTask(taskObj);
            th.updateTaskStatus();
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
        } finally {
            span.end();
        }
    }
}
