package io.falcon.assessment.messagepool;

import io.falcon.assessment.message.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MessagePublisher {

    private final ConcurrentLinkedQueue<String> queue;
    private final ApplicationContext ctx;
    private final ExecutorService executorService;

    public MessagePublisher(final ConcurrentLinkedQueue<String> queue, final ApplicationContext ctx, final int threadPoolSize) {
        this.queue = queue;
        this.ctx = ctx;
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        log.info("configured message pool with {} threads", threadPoolSize);
    }

    public void publish(final String content) throws Exception {
        publishMessage(content);
        invokeConsumer();
    }

    private void publishMessage(String content) {
        log.trace("publish message --> {} ", content);
        queue.offer(content);
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    private void invokeConsumer() throws Exception {
        MessageService messageService = ctx.getBean(MessageService.class);
        MessageConsumer consumer = new MessageConsumer(queue, messageService);
        executorService.execute(consumer);
    }

}
