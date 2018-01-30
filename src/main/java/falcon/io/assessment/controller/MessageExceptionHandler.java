package falcon.io.assessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(annotations = RestController.class)
public class MessageExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorMessage badRequest(IllegalArgumentException ex) {
        return new ErrorMessage(ex.getMessage());
    }

}
