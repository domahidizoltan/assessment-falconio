package io.falcon.assessment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.falcon.assessment.message.MessageRepository;
import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.messaging.NotificationComposite;
import io.falcon.assessment.messaging.NotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public NotificationComposite notifications(List<NotificationSender> notificationSenders) {
        return new NotificationComposite(notificationSenders);
    }

    @Bean
    public MessageService messageService(MessageRepository messageRepository, NotificationComposite notifications, Clock clock, ObjectMapper customObjectMapper) {
        return new MessageService(messageRepository, notifications, clock, customObjectMapper);
    }


}
