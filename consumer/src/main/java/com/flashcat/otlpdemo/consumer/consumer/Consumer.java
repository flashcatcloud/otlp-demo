package com.flashcat.otlpdemo.consumer.consumer;

import com.flashcat.otlpdemo.consumer.model.Task;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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

    public void doConsumer() {
        try {
            consumer.subscribe(Collections.singletonList(this.topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                if (records.isEmpty()) {
                    continue;
                }
                for (ConsumerRecord<String, String> record : records) {
                    doProcess(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WithSpan("doProcess")
    private void doProcess(ConsumerRecord<String, String> record) {
        String json = record.value();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Task taskObj = gson.fromJson(json, Task.class);
        System.out.println(taskObj.toString());
    }
}
