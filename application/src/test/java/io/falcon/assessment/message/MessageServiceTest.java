package io.falcon.assessment.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.assessment.messaging.send.NotificationComposite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static io.falcon.assessment.helper.MessageHelper.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    private ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
    private ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

    private MessageService messageService;

    @Before
    public void setUp() {
        messageService = new MessageService(messageRepositoryMock, notificationsMock, clockMock, objectMapper);
        given(clockMock.instant()).willReturn(NOW);
    }

    @Test
    public void shouldSaveMessageWithCurrentTime() {
        Message newMessage = makeMessage(null, ANY_CONTENT);
        Message savedMessage = makeMessage(ANY_ID, ANY_CONTENT);
        given(messageRepositoryMock.save(eq(newMessage))).willReturn(savedMessage);

        Message actualMessages = messageService.save(ANY_CONTENT);

        verify(notificationsMock, times(1)).send(eq(actualMessages));
        assertMessage(actualMessages, ANY_ID, ANY_CONTENT);
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
    public void shouldGetLatestMessages() {
        Message firstMessage = makeMessage("1", ANY_CONTENT);
        Message secondMessage = makeMessage("2", ANY_CONTENT);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest expectedPage = PageRequest.of(0, 20, sort);

        given(messageRepositoryMock.findByCreateTimeBefore(any(Instant.class), any(Pageable.class)))
            .willReturn(Arrays.asList(firstMessage, secondMessage));

        List<Message> actualMessages = messageService.getMessagesBeforeCreateTime(null, 20);

        assertEquals(actualMessages.size(), 2);
        assertMessage(actualMessages.get(0), "1", ANY_CONTENT);
        assertMessage(actualMessages.get(1), "2", ANY_CONTENT);

        verify(messageRepositoryMock, times(1))
            .findByCreateTimeBefore(instantCaptor.capture(), pageableCaptor.capture());
        assertEquals(NOW, instantCaptor.getValue());
        assertEquals(expectedPage, pageableCaptor.getValue());
    }


    @Test
    public void shouldGetMessagesFromCreateTimeWithLimit() {
        Message firstMessage = makeMessage("1", ANY_CONTENT);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest expectedPage = PageRequest.of(0, 1, sort);
        Instant expectedTime = Instant.now().minusSeconds(10);

        given(messageRepositoryMock.findByCreateTimeBefore(any(Instant.class), any(Pageable.class)))
            .willReturn(Arrays.asList(firstMessage));

        List<Message> actualMessages = messageService.getMessagesBeforeCreateTime(expectedTime, 1);

        assertEquals(actualMessages.size(), 1);
        assertMessage(actualMessages.get(0), "1", ANY_CONTENT);

        verify(messageRepositoryMock, times(1))
            .findByCreateTimeBefore(instantCaptor.capture(), pageableCaptor.capture());
        assertEquals(expectedTime, instantCaptor.getValue());
        assertEquals(expectedPage, pageableCaptor.getValue());
    }

    private void assertMessage(Message message, String id, String content) {
        assertEquals(message.getId(), id);
        assertEquals(message.getContent(), content);
        Assert.assertEquals(message.getCreateTime(), NOW);
    }

}
