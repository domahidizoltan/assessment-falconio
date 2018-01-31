package io.falcon.assessment.messagepool;

import io.falcon.assessment.message.MessageService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class MessageConsumer implements Runnable {

    private final ConcurrentLinkedQueue<String> queue;
    private final MessageService messageService;

    public MessageConsumer(final ConcurrentLinkedQueue queue, final MessageService messageService) {
        this.queue = queue;
        this.messageService = messageService;
    }

    public void run() {
        while (true) {
            try {
                waitForMessage(queue);
                saveMessage(queue);
            } catch (InterruptedException ex) {
                log.error("Thread {} was interrupted", Thread.currentThread().getName());
            }
        }
    }

    private void waitForMessage(final ConcurrentLinkedQueue<String> queue) throws InterruptedException {
        if (queue.isEmpty()) {
            synchronized (this.queue) {
                queue.wait(100);
            }
        }
    }

    private void saveMessage(final ConcurrentLinkedQueue<String> queue) {
        String content = queue.poll();
        if (content != null) {
            log.trace("consume message <-- {} ", content);
            messageService.save(content);
        }
    }

}
