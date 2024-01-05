package com.flashcat.otlpdemo.consumer.service.http;

import com.flashcat.otlpdemo.consumer.model.Task;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.httpclient.JavaHttpClientTelemetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TaskHTTPService {

    @Value("${otlpdemo.console.addr}")
    private String addr;

    private Task task;
    private final HttpClient client;

    public TaskHTTPService() {
        this.client = JavaHttpClientTelemetry.builder(GlobalOpenTelemetry.get()).build().newHttpClient(HttpClient.newBuilder().build());
    }

    @WithSpan("updateTaskStatusByAPI")
    public void updateTaskStatus() throws IOException, InterruptedException, URISyntaxException {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        String task = gson.toJson(this.task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(addr + "/api/v1/tasks/updateStatus"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException("Unexpected HTTP response: " + response.statusCode());
        }
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
