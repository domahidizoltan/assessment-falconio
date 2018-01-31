package io.falcon.assessment.messagepool;

import io.falcon.assessment.message.Message;
import io.falcon.assessment.message.MessageRepository;
import io.falcon.assessment.messaging.send.NotificationComposite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.falcon.assessment.helper.MessageHelper.makeMessage;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublishConsumeITest {

    @MockBean
    private MessageRepository messageRepositoryMock;

    @MockBean
    private NotificationComposite notificationCompositeMock;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private ConcurrentLinkedQueue<String> pcQueue;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);

    @Test
    public void shouldPublishMultipleMessages() throws Exception {
        ArrayList<String> contents = new ArrayList<>();
        contents.add("{\"field1\":\"value1\"}");
        contents.add("{\"field2\":\"value2\"}");
        contents.add("{\"field3\":\"value3\"}");
        contents.add("{\"field4\":\"value4\"}");
        prepareRepositoryMock(contents);

        contents.forEach(content -> {
            executorService.submit(runPublish(content));
        });

        do {
            Thread.sleep(100);
        } while (!pcQueue.isEmpty());

        verify(messageRepositoryMock, times(4)).save(captor.capture());
        List<String> actualContents = getMessageContentsFromCaptor(captor);
        assertThat(actualContents, containsInAnyOrder(contents.toArray()));
    }

    private void prepareRepositoryMock(List<String> contents) {
        contents.forEach(content -> {
            Message newMessage = makeMessage("1", content);
            given(messageRepositoryMock.save(any(Message.class))).willReturn(newMessage);
        });
    }

    private Runnable runPublish(final String content) {
        return () -> {
            try {
                messagePublisher.publish(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private List<String> getMessageContentsFromCaptor(ArgumentCaptor<Message> captor) {
        return captor.getAllValues().stream()
            .map(Message::getContent)
            .collect(Collectors.toList());
    }

}
