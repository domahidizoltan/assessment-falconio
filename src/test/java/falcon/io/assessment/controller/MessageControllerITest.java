package falcon.io.assessment.controller;

import falcon.io.assessment.message.Message;
import falcon.io.assessment.message.MessageService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;
import java.util.Optional;

import static falcon.io.assessment.helper.MessageHelper.*;
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
    private String JSON_INPUT_FORMAT = "{\"content\":\"%s\"}";
    private String EMPTY_JSON_INPUT = "{\"content\":\"\"}";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageServiceMock;

    @Test
    public void shouldSaveMessage() throws Exception {
        Message savedMessage = makeMessage(ANY_ID, ANY_CONTENT);
        given(messageServiceMock.save(eq(ANY_CONTENT))).willReturn(savedMessage);
        RequestBuilder postRequest = post(MESSAGES_URL)
            .content(String.format(JSON_INPUT_FORMAT, ANY_CONTENT))
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(ANY_ID)))
            .andExpect(jsonPath("$.content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$.createTime", is(NOW.toString())));
    }

    @Test
    public void shouldReturnBadRequestOnInvalidContent() throws Exception {
        String errorMessage = "Invalid input";
        given(messageServiceMock.save(anyString()))
            .willThrow(new IllegalArgumentException(errorMessage));
        RequestBuilder invalidPostRequest = post(MESSAGES_URL)
            .content(EMPTY_JSON_INPUT)
            .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(invalidPostRequest)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    @Ignore
    public void shouldReturnAllMessages() throws Exception {
        Message firstMessage = makeMessage("1", ANY_CONTENT + "1");
        Message secondMessage = makeMessage("2", ANY_CONTENT + "2");
        given(messageServiceMock.getAll())
            .willReturn(Arrays.asList(firstMessage, secondMessage));

        //TODO use custom matcher
        RequestBuilder getAllRequest = get(MESSAGES_URL);
        mvc.perform(getAllRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("1")))
            .andExpect(jsonPath("$[0].content", is(ANY_CONTENT + "1")))
            .andExpect(jsonPath("$[0].createTime", is(NOW)))
            .andExpect(jsonPath("$[1].id", is("2")))
            .andExpect(jsonPath("$[1].content", is(ANY_CONTENT + "2")))
            .andExpect(jsonPath("$[1].createTime", is(NOW.toString())));
    }

    @Test
    public void shouldReturnMessageById() throws Exception {
        given(messageServiceMock.getById(eq(ANY_ID)))
            .willReturn(Optional.of(ANY_MESSAGE));

        RequestBuilder getByIdRequest = get(MESSAGES_URL + ANY_ID);
        mvc.perform(getByIdRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ANY_ID)))
            .andExpect(jsonPath("$.content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$.createTime", is(NOW.toString())));
    }

    @Test
    public void shouldReturnNotFoundWhenGettingNonexistentMessageById() throws Exception {
        given(messageServiceMock.getById(eq(ANY_ID)))
            .willReturn(Optional.empty());

        RequestBuilder getByIdRequest = get(MESSAGES_URL + ANY_ID);
        mvc.perform(getByIdRequest)
            .andExpect(status().isNotFound());
    }


    @Test
    @Ignore
    public void shouldReturnPageOfMessages() throws Exception {
        PageRequest pageable = PageRequest.of(0, 1);
        PageImpl<Message> messagePage = new PageImpl<>(Arrays.asList(ANY_MESSAGE), pageable, 3);
        given(messageServiceMock.getPage(eq(pageable))).willReturn(messagePage);

        RequestBuilder getPageRequest = get(MESSAGES_URL + "?page=0&size=1");
        mvc.perform(getPageRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ANY_ID)))
            .andExpect(jsonPath("$.content", is(ANY_CONTENT)))
            .andExpect(jsonPath("$.createTime", is(NOW)))
            .andExpect(jsonPath("$.totalElements", is(3)))
            .andExpect(jsonPath("$.totalPages", is(3)))
            .andExpect(jsonPath("$.size", is(1)))
            .andExpect(jsonPath("$.number", is(0)));

    }

}
