package com.amir.eventmanager;

import com.amir.eventmanager.message.EventChangeKafkaMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(new DefaultSslBundleRegistry());
        ProducerFactory producerFactory = new DefaultKafkaProducerFactory(props);
        return new KafkaTemplate<>(producerFactory);
    }
}
