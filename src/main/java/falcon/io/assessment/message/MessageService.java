package falcon.io.assessment.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class MessageService {

    private MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(String content) {
    }

    public Message getById(String id) {
        return null;
    }

    public List<Message> getAll() {
        return null;
    }

    public Page<Message> getPage(PageRequest pageRequest) {
        return null;
    }
}
