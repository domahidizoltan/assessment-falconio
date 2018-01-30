package falcon.io.assessment.messaging;

import falcon.io.assessment.message.Message;

public interface NotificationSender {

    Message notify(Message message);

}
