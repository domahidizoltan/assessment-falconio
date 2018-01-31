package io.falcon.assessment.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.assessment.messaging.send.NotificationComposite;
import io.falcon.assessment.helper.MessageHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepositoryMock;

    @Mock
    private NotificationComposite notificationsMock;

    @Mock
    private Clock clockMock;

    ObjectMapper objectMapper = new ObjectMapper();

    private MessageService messageService;

    @Before
    public void setUp() {
        messageService = new MessageService(messageRepositoryMock, notificationsMock, clockMock, objectMapper);
        given(clockMock.instant()).willReturn(MessageHelper.NOW);
    }

    @Test
    public void shouldSaveMessageWithCurrentTime() {
        Message newMessage = MessageHelper.makeMessage(null, MessageHelper.ANY_CONTENT);
        Message savedMessage = MessageHelper.makeMessage(MessageHelper.ANY_ID, MessageHelper.ANY_CONTENT);
        given(messageRepositoryMock.save(eq(newMessage))).willReturn(savedMessage);

        Message expectedMessage = messageService.save(MessageHelper.ANY_CONTENT);

        verify(notificationsMock, times(1)).send(eq(expectedMessage));
        assertMessage(expectedMessage, MessageHelper.ANY_ID, MessageHelper.ANY_CONTENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMessageIsEmptyOnSave() {
        messageService.save("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMessageIsNullOnSave() {
        messageService.save(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMessageIsNotValidJson() {
        messageService.save("invalidJson");
    }

    @Test
    public void shouldGetAllMessages() {
        Message firstMessage = MessageHelper.makeMessage("1", MessageHelper.ANY_CONTENT);
        Message secondMessage = MessageHelper.makeMessage("2", MessageHelper.ANY_CONTENT);
        Message thirdMessage = MessageHelper.makeMessage("3", MessageHelper.ANY_CONTENT);
        given(messageRepositoryMock.findAll()).willReturn(Arrays.asList(firstMessage, secondMessage, thirdMessage));

        List<Message> expectedMessages = messageService.getAllMessage();

        assertEquals(expectedMessages.size(), 3);
        assertMessage(expectedMessages.get(0), "1", MessageHelper.ANY_CONTENT);
        assertMessage(expectedMessages.get(1), "2", MessageHelper.ANY_CONTENT);
        assertMessage(expectedMessages.get(2), "3", MessageHelper.ANY_CONTENT);
    }

    private void assertMessage(Message message, String id, String content) {
        assertEquals(message.getId(), id);
        assertEquals(message.getContent(), content);
        Assert.assertEquals(message.getCreateTime(), MessageHelper.NOW);
    }

}
