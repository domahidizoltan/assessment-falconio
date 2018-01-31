package io.falcon.assessment.messaging.send;

import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.messaging.receive.WebSocketMessageReceiver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static io.falcon.assessment.helper.MessageHelper.ANY_CONTENT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class WebSocketMessageReceiverTest {

    @Mock
    private MessageService messageServiceMock;

    @Mock
    private SimpMessagingTemplate messagingTemplateMock;

    private WebSocketMessageReceiver messageReceiver;

    @Before
    public void setUp() {
        messageReceiver = new WebSocketMessageReceiver(messageServiceMock, messagingTemplateMock);
    }

    @Test
    public void shouldReceiveMessage() {
        messageReceiver.onMessage(ANY_CONTENT);

        verify(messageServiceMock, times(1)).save(eq(ANY_CONTENT));
    }

    @Test
    public void shouldReturnErrorResponseWhenExceptionOccurred() {
        String errorMessage = "error message on save";
        given(messageServiceMock.save(anyString())).willThrow(new IllegalArgumentException(errorMessage));

        messageReceiver.onMessage(ANY_CONTENT);

        verify(messagingTemplateMock, times(1))
            .convertAndSend(eq("/topic/errors"), eq(errorMessage));
    }

}
