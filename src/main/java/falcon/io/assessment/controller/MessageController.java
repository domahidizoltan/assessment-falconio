package falcon.io.assessment.controller;

import falcon.io.assessment.message.Message;
import falcon.io.assessment.message.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/messages")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public List<Message> getAllMessage() {
        return messageService.getAllMessage();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Message saveMessage(@RequestBody String content) {
        return messageService.save(content);
    }
}
