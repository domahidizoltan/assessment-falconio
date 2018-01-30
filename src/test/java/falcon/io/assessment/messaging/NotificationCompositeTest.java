package falcon.io.assessment.messaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static falcon.io.assessment.helper.MessageHelper.ANY_MESSAGE;
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
        notificationComposite.send(ANY_MESSAGE);

        verify(firstNotificationSenderMock, times(1)).notify(eq(ANY_MESSAGE));
        verify(secondNotificationSenderMock, times(1)).notify(eq(ANY_MESSAGE));
    }

}
