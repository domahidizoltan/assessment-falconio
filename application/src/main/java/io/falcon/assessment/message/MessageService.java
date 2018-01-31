package io.falcon.assessment.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.assessment.messaging.NotificationComposite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

@Slf4j
public class MessageService {

    private MessageRepository messageRepository;
    private NotificationComposite notifications;
    private Clock clock;
    private ObjectMapper objectMapper;

    public MessageService(MessageRepository messageRepository, NotificationComposite notifications, Clock clock, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.notifications = notifications;
        this.clock = clock;
        this.objectMapper = objectMapper;
    }

    public Message save(String content) {
        log.debug("saving message with content: " + content);
        content = sanitize(content);
        validate(content);

        Message message = toMessage(content);
        Message savedMessage = messageRepository.save(message);
        notifications.send(savedMessage);
        return savedMessage;
    }

    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    private Message toMessage(String content) {
        return Message.builder()
            .content(content)
            .createTime(clock.instant())
            .build();
    }

    private String sanitize(String content) {
        if (content != null) {
            content = content.trim();
        }
        return content;
    }

    private void validate(String content) {
        Assert.hasLength(content, "Message content must not be null or empty!");
        try {
            objectMapper.readTree(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("Message must be valid Json!");
        }
    }

}