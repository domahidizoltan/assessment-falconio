package io.falcon.assessment.config;

import io.falcon.assessment.messagepool.MessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class MessagePoolConfig {

    @Value("${app.config.thread-pool-size:10}")
    private int threadPoolSize;

    @Bean
    public ConcurrentLinkedQueue<String> pcQueue() {
        return new ConcurrentLinkedQueue<String>();
    }

    @Bean
    public MessagePublisher publisher(ConcurrentLinkedQueue<String> pcQueue, ApplicationContext ctx) {
        return new MessagePublisher(pcQueue, ctx, threadPoolSize);
    }

}
