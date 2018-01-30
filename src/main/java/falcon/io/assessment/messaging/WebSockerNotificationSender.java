package falcon.io.assessment.messaging;

import falcon.io.assessment.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSockerNotificationSender implements NotificationSender {

    private SimpMessagingTemplate messagingTemplate;

    public WebSockerNotificationSender(SimpMessagingTemplate template) {
        this.messagingTemplate = template;
    }

    public Message notify(Message message) {
        log.debug("sending message on websocket: " + message);
        messagingTemplate.convertAndSend("/topic/messages", message);
        return message;
    }

}
