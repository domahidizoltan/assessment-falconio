package io.falcon.assessment.controller;

import io.falcon.assessment.message.Message;
import io.falcon.assessment.message.MessageService;
import io.falcon.assessment.helper.MessageHelper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    public void shouldSaveMessage() throws Exception {
        Message savedMessage = MessageHelper.makeMessage(MessageHelper.ANY_ID, MessageHelper.ANY_CONTENT);
        given(messageServiceMock.save(eq(MessageHelper.ANY_CONTENT))).willReturn(savedMessage);
        RequestBuilder postRequest = post(MESSAGES_URL)
            .content(MessageHelper.ANY_CONTENT)
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(MessageHelper.ANY_ID)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(MessageHelper.ANY_CONTENT)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createTime", Matchers.is(MessageHelper.NOW.toString())));
    }

    @Test
    public void shouldReturnBadRequestOnInvalidContent() throws Exception {
        String errorMessage = "Invalid input";
        given(messageServiceMock.save(anyString()))
            .willThrow(new IllegalArgumentException(errorMessage));
        RequestBuilder invalidPostRequest = post(MESSAGES_URL)
            .content("invalidJson")
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(invalidPostRequest)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    public void shouldReturnAllMessages() throws Exception {
        Message firstMessage = MessageHelper.makeMessage("1", MessageHelper.ANY_CONTENT);
        Message secondMessage = MessageHelper.makeMessage("2", MessageHelper.ANY_CONTENT);
        given(messageServiceMock.getAllMessage())
            .willReturn(Arrays.asList(firstMessage, secondMessage));

        RequestBuilder getAllRequest = get(MESSAGES_URL);
        mvc.perform(getAllRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("1")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].content", Matchers.is(MessageHelper.ANY_CONTENT)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createTime", Matchers.is(MessageHelper.NOW.toString())))
            .andExpect(jsonPath("$[1].id", is("2")))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].content", Matchers.is(MessageHelper.ANY_CONTENT)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].createTime", Matchers.is(MessageHelper.NOW.toString())));
    }

}
