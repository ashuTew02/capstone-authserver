package com.capstone.authServer.config;

import org.springframework.context.annotation.Configuration;

/**
 * Separate class to hold all Kafka-related configurations
 * (producer factory, consumer factory, topic creation, etc.).
 */
@Configuration
public class KafkaConfig {

    // @Value("${kafka.topics.scan-request}")
    // private String topicName;
    // @Bean
    // public NewTopic scanTopic() {
    //     return TopicBuilder.name(topicName)
    //             .partitions(3)
    //             .replicas(1)
    //             .build();
    // }
    
    /*
      If you wish to store producer properties in code (instead of application.yaml),
      you can define ProducerFactory and KafkaTemplate beans here.
    */
}
