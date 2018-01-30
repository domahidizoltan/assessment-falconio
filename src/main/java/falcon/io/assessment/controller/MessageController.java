package falcon.io.assessment.controller;

import falcon.io.assessment.message.Message;
import falcon.io.assessment.message.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/messages")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public @ResponseBody Message getMessageById(@PathVariable String id) {
        return messageService.getById(id).orElseThrow(() -> new NoSuchElementException("Message not found."));
    }

    @GetMapping("/")
    public Page<Message> getMessages(Pageable pageRequest) {
        return messageService.getPage(pageRequest);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Message saveMessage(@RequestBody MessageDto messageDto) {
        return messageService.save(messageDto.getContent());
    }
}
