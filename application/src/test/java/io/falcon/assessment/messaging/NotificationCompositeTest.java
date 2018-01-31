package io.falcon.assessment.messaging;

import io.falcon.assessment.helper.MessageHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationCompositeTest {

    @Mock
    private NotificationSender firstNotificationSenderMock;

    @Mock
    private NotificationSender secondNotificationSenderMock;

    private NotificationComposite notificationComposite;

    @Before
    public void setUp() {
        List<NotificationSender> notificationSenders = Arrays.asList(firstNotificationSenderMock, secondNotificationSenderMock);
        notificationComposite = new NotificationComposite(notificationSenders);
    }

    @Test
    public void shouldSendNotifiations() {
        notificationComposite.send(MessageHelper.ANY_MESSAGE);

        verify(firstNotificationSenderMock, times(1)).notify(eq(MessageHelper.ANY_MESSAGE));
        verify(secondNotificationSenderMock, times(1)).notify(eq(MessageHelper.ANY_MESSAGE));
    }

}
