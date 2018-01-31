package io.falcon.assessment.messaging.send;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static io.falcon.assessment.helper.MessageHelper.ANY_MESSAGE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class WebSocketNotificationSenderTest {

    @Mock
    private SimpMessagingTemplate messagingTemplateMock;

    private NotificationSender notificationSender;

    @Before
    public void setUp() {
        notificationSender = new WebSockerNotificationSender(messagingTemplateMock);
    }

    @Test
    public void shouldSendNotificationOverWebSocket() {
        notificationSender.notify(ANY_MESSAGE);

        verify(messagingTemplateMock, times(1))
            .convertAndSend(eq("/topic/messages"), eq(ANY_MESSAGE));
    }

}
