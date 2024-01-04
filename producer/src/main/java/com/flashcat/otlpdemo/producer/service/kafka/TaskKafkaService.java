package com.flashcat.otlpdemo.producer.service.kafka;

import com.flashcat.otlpdemo.producer.model.Task;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TaskKafkaService {
    @Autowired
    private Producer<String, String> kafkaProducer;

    private Task task;
    @Value("${otlpdemo.topic}")
    private String topic;

    @WithSpan("sendTaskToKafka")
    public void sendTask() throws InterruptedException, ExecutionException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        String taskObj = gson.toJson(task);
        ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, taskObj);
        this.kafkaProducer.send(msg).get();
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
