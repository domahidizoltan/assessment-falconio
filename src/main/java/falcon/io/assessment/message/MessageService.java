package falcon.io.assessment.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

public class MessageService {

    private MessageRepository messageRepository;
    private Clock clock;

    public MessageService(MessageRepository messageRepository, Clock clock) {
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    public Message save(String content) {
        Assert.hasLength(content, "Message content must not be null or empty!");
        Message message = toMessage(content);
        return messageRepository.save(message);
    }

    public Optional<Message> getById(String id) {
        return messageRepository.findById(id);
    }

    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    public Page<Message> getPage(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    private Message toMessage(String content) {
        return Message.builder()
            .content(content)
            .createTime(clock.instant())
            .build();
    }
}
