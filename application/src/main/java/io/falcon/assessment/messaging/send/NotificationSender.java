package io.falcon.assessment.messaging.send;

import io.falcon.assessment.message.Message;

public interface NotificationSender {

    Message notify(Message message);

}
