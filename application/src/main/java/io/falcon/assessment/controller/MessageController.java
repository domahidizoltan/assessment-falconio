package io.falcon.assessment.controller;

import io.falcon.assessment.message.Message;
import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.messagepool.MessagePublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/messages")
public class MessageController {

    private MessageService messageService;
    private MessagePublisher messagePublisher;

    public MessageController(MessageService messageService, MessagePublisher messagePublisher) {
        this.messageService = messageService;
        this.messagePublisher = messagePublisher;
    }

    @GetMapping("/")
    public List<Message> getAllMessage() {
        return messageService.getAllMessage();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody String content) throws Exception {
        messagePublisher.publish(content);
    }
}
