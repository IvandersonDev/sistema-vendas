package com.example.vendas.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.contracts.KafkaTopics;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic pedidosTopic() {
        // 3 partitions para paralelismo; 1 replica para ambiente local
        return new NewTopic(KafkaTopics.PEDIDOS_EVENTS, 3, (short) 1);
    }
}
