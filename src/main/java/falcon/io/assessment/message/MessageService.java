package falcon.io.assessment.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

@Slf4j
public class MessageService {

    private MessageRepository messageRepository;
    private Clock clock;
    private ObjectMapper objectMapper;

    public MessageService(MessageRepository messageRepository, Clock clock, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.clock = clock;
        this.objectMapper = objectMapper;
    }

    public Message save(String content) {
        Assert.hasLength(content, "Message content must not be null or empty!");
        try {
            objectMapper.readTree(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("Message must have valid Json!");
        }

        Message message = toMessage(content);
        return messageRepository.save(message);
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
}
