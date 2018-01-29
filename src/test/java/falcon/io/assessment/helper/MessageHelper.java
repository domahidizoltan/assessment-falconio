package falcon.io.assessment.helper;

import falcon.io.assessment.message.Message;

import java.time.Instant;

public class MessageHelper {

    private MessageHelper() {
    }

    public static final String ANY_ID = "1";
    public static final String ANY_CONTENT = "any content";
    public static final Instant NOW = Instant.now();
    public static final Message ANY_MESSAGE = makeMessage(ANY_ID, ANY_CONTENT);

    public static Message makeMessage(String id, String content) {
        return Message.builder()
            .id(id)
            .content(content)
            .createTime(NOW)
            .build();
    }


}