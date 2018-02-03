package io.falcon.assessment.controller;

import io.falcon.assessment.message.Message;
import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.messagepool.MessagePublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;

import static io.falcon.assessment.helper.MessageHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
public class MessageControllerITest {

    private static final String MESSAGES_URL = "/messages/";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageServiceMock;

    @MockBean
    private MessagePublisher messagePublisherMock;

    @Test
    public void shouldSaveMessage() throws Exception {
        Message savedMessage = makeMessage(ANY_ID, ANY_CONTENT);
        RequestBuilder postRequest = post(MESSAGES_URL)
            .content(ANY_CONTENT)
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(postRequest)
            .andExpect(status().isCreated());

        verify(messagePublisherMock, times(1)).publish(eq(ANY_CONTENT));
    }

    @Test
    public void shouldReturnBadRequestOnInvalidContent() throws Exception {
        String errorMessage = "Invalid input";
        willThrow(new IllegalArgumentException(errorMessage))
            .given(messagePublisherMock).publish(anyString());
        RequestBuilder invalidPostRequest = post(MESSAGES_URL)
            .content("invalidJson")
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(invalidPostRequest)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    public void shouldReturnLatestMessagesWhenNoParametersSpecified() throws Exception {
        Message firstMessage = makeMessage("1", ANY_CONTENT);
        Message secondMessage = makeMessage("2", ANY_CONTENT);
        given(messageServiceMock.getMessagesBeforeCreateTime(null, 20))
            .willReturn(Arrays.asList(firstMessage, secondMessage));

        RequestBuilder getRequest = get(MESSAGES_URL);
        mvc.perform(getRequest)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("1")))
            .andExpect(jsonPath("$[0].content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$[0].createTime", is(NOW.toString())))
            .andExpect(jsonPath("$[1].id", is("2")))
            .andExpect(jsonPath("$[1].content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$[1].createTime", is(NOW.toString())));
    }

    @Test
    public void shouldReturnMessagesFromGivenTimeWithLimit() throws Exception {
        Message firstMessage = makeMessage("1", ANY_CONTENT);
        given(messageServiceMock.getMessagesBeforeCreateTime(NOW, 1))
            .willReturn(Arrays.asList(firstMessage));

        RequestBuilder getRequest = get(MESSAGES_URL + "?createTime={createTime}&limit=1", NOW.toString());
        mvc.perform(getRequest)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is("1")))
            .andExpect(jsonPath("$[0].content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$[0].createTime", is(NOW.toString())));
    }

}
