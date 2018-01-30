package falcon.io.assessment.messaging;

import falcon.io.assessment.message.Message;

import java.util.List;

public class NotificationComposite {

    private List<NotificationSender> notificationSenders;

    public NotificationComposite(List<NotificationSender> notificationSenders) {
        this.notificationSenders = notificationSenders;
    }

    public void send(Message message) {
        notificationSenders.forEach(sender -> sender.notify(message));
    }

}
