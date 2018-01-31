package io.falcon.assessment.messaging;

import io.falcon.assessment.message.Message;

public interface NotificationSender {

    Message notify(Message message);

}
