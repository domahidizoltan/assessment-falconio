package io.falcon.assessment.controller;

import io.falcon.assessment.message.Message;
import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.messagepool.MessagePublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static io.falcon.assessment.message.Message.DATETIME_FORMAT;

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
    public List<Message> getMessages(
        @RequestParam(required = false) @DateTimeFormat(pattern = DATETIME_FORMAT) Instant createTime,
        @RequestParam(required = false, defaultValue = "20") Integer limit) {

        return messageService.getMessagesBeforeCreateTime(createTime, limit);
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody String content) throws Exception {
        messagePublisher.publish(content);
    }
}
