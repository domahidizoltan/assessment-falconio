package io.falcon.assessment.messaging.receive;

import io.falcon.assessment.message.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageReceiver {

    private MessageService messageService;
    private SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageReceiver(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/save-message")
    public void onMessage(String content) {
        try {
            messageService.save(content);
        } catch (RuntimeException ex) {
            messagingTemplate.convertAndSend("/topic/errors", ex.getMessage());
        }
    }
}
