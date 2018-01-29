package falcon.io.assessment.message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    private MessageService messageService;

    @Before
    public void setUp() {
        messageService = new MessageService(messageRepositoryMock, clockMock);
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


    @Test
    public void shouldGetMessageById() {
        given(messageRepositoryMock.findById(eq(ANY_ID)))
            .willReturn(Optional.of(ANY_MESSAGE));

        Optional<Message> expectedMessage = messageService.getById(ANY_ID);
        assertMessage(expectedMessage.get(), ANY_ID, ANY_CONTENT);
    }

    @Test
    public void shouldGetAllMessages() {
        Message firstMessage = makeMessage("1", ANY_CONTENT + "1");
        Message secondMessage = makeMessage("2", ANY_CONTENT + "2");
        Message thirdMessage = makeMessage("3", ANY_CONTENT + "3");
        given(messageRepositoryMock.findAll()).willReturn(Arrays.asList(firstMessage, secondMessage, thirdMessage));

        List<Message> expectedMessages = messageService.getAll();

        assertEquals(expectedMessages.size(), 3);
        assertMessage(expectedMessages.get(0), "1", ANY_CONTENT + "1");
        assertMessage(expectedMessages.get(1), "2", ANY_CONTENT + "2");
        assertMessage(expectedMessages.get(2), "3", ANY_CONTENT + "3");
    }

    @Test
    public void shouldGetFirstPageOfMessages() {
        Message firstMessage = makeMessage("1", ANY_CONTENT + "1");
        Message secondMessage = makeMessage("2", ANY_CONTENT + "2");
        List<Message> pageResult = Arrays.asList(firstMessage, secondMessage);
        PageRequest pageRequest = prepareRepoForPage(pageResult, 0);

        Page<Message> expectedMessages = messageService.getPage(pageRequest);

        assertPage(expectedMessages, 3, 2, 2, 0);
        assertMessage(expectedMessages.getContent().get(0), "1", ANY_CONTENT + "1");
        assertMessage(expectedMessages.getContent().get(1), "2", ANY_CONTENT + "2");
    }

    @Test
    public void shouldGetLastPageOfMessages() {
        Message thirdMessage = makeMessage("3", ANY_CONTENT + "3");
        List<Message> pageResult = Arrays.asList(thirdMessage);
        PageRequest pageRequest = prepareRepoForPage(pageResult, 1);

        Page<Message> expectedMessages = messageService.getPage(pageRequest);

        assertPage(expectedMessages, 3, 2, 2, 1);
        assertMessage(expectedMessages.getContent().get(0), "3", ANY_CONTENT + "3");
    }

    private void assertMessage(Message message, String id, String content) {
        assertEquals(message.getId(), id);
        assertEquals(message.getContent(), content);
        assertEquals(message.getCreateTime(), NOW);
    }

    private PageRequest prepareRepoForPage(List<Message> pageResult, int page) {
        PageRequest pageRequest = PageRequest.of(page, 2);
        PageImpl<Message> messagePage = new PageImpl<>(pageResult, pageRequest, 3);
        given(messageRepositoryMock.findAll(pageRequest)).willReturn(messagePage);
        return pageRequest;
    }

    private void assertPage(Page<?> page, int totalElements, int totalPages, int size, int number) {
        assertEquals(page.getTotalElements(), totalElements);
        assertEquals(page.getTotalPages(), totalPages);
        assertEquals(page.getSize(), size);
        assertEquals(page.getNumber(), number);
    }


}
