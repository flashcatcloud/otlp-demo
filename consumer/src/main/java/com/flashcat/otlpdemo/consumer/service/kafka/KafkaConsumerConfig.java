package com.flashcat.otlpdemo.consumer.service.kafka;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.TracingConsumerInterceptor;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.TracingProducerInterceptor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.key-serializer}")
    private String keySerializer;
    @Value("${kafka.consumer.value-serializer}")
    private String valueSerializer;
    @Value("${kafka.consumer.group_id}")
    private String groupID;

    @Bean
    public Consumer<String, String> kafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);

        props.setProperty(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingConsumerInterceptor.class.getName());

        Consumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
        return KafkaTelemetry.create(GlobalOpenTelemetry.get()).wrap(kafkaConsumer);
    }
}
