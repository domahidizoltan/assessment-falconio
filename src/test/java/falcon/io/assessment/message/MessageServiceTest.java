package falcon.io.assessment.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import static falcon.io.assessment.helper.MessageHelper.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepositoryMock;

    @Mock
    private Clock clockMock;

    ObjectMapper objectMapper = new ObjectMapper();

    private MessageService messageService;

    @Before
    public void setUp() {
        messageService = new MessageService(messageRepositoryMock, clockMock, objectMapper);
        given(clockMock.instant()).willReturn(NOW);
    }

    @Test
    public void shouldSaveMessageWithCurrentTime() {
        Message newMessage = makeMessage(null, ANY_CONTENT);
        Message savedMessage = makeMessage(ANY_ID, ANY_CONTENT);
        given(messageRepositoryMock.save(eq(newMessage))).willReturn(savedMessage);

        Message expectedMessage = messageService.save(ANY_CONTENT);

        assertMessage(expectedMessage, ANY_ID, ANY_CONTENT);
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
        Message firstMessage = makeMessage("1", ANY_CONTENT);
        Message secondMessage = makeMessage("2", ANY_CONTENT);
        Message thirdMessage = makeMessage("3", ANY_CONTENT);
        given(messageRepositoryMock.findAll()).willReturn(Arrays.asList(firstMessage, secondMessage, thirdMessage));

        List<Message> expectedMessages = messageService.getAllMessage();

        assertEquals(expectedMessages.size(), 3);
        assertMessage(expectedMessages.get(0), "1", ANY_CONTENT);
        assertMessage(expectedMessages.get(1), "2", ANY_CONTENT);
        assertMessage(expectedMessages.get(2), "3", ANY_CONTENT);
    }

    private void assertMessage(Message message, String id, String content) {
        assertEquals(message.getId(), id);
        assertEquals(message.getContent(), content);
        assertEquals(message.getCreateTime(), NOW);
    }

}
