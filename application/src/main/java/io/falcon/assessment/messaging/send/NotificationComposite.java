package io.falcon.assessment.messaging.send;

import io.falcon.assessment.message.Message;

import java.util.List;

public class NotificationComposite {

    private final List<NotificationSender> notificationSenders;

    public NotificationComposite(final List<NotificationSender> notificationSenders) {
        this.notificationSenders = notificationSenders;
    }

    public void send(final Message message) {
        notificationSenders.forEach(sender -> sender.notify(message));
    }

}
