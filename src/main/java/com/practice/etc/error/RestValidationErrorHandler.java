package com.practice.etc.error;

import com.practice.controller.EmailController;
import com.practice.dto.GenericResponseDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: tomer
 */
@ControllerAdvice
public class RestValidationErrorHandler {
    private static final Logger logger = Logger.getLogger(RestValidationErrorHandler.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EmailController emailController;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<GenericResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException notValidException) {
        logger.error("bean validation error", notValidException);
        BindingResult result = notValidException.getBindingResult();
        String errorMsg = messageSource.getMessage(result.getFieldError(), LocaleContextHolder.getLocale());
        return new ResponseEntity<GenericResponseDto>(GenericResponseDto.failure(errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LogicalException.class)
    @ResponseBody
    public ResponseEntity<GenericResponseDto> handleLogicalException(LogicalException logicalException) {
        String errMsg = logicalException.getMessage() == null ? "general error" : logicalException.getMessage();
        logger.error(errMsg, logicalException);
        emailController.sendInternalErrorMail("handleLogicalException", logicalException);

        String nlsMsg = messageSource.getMessage(logicalException.getNlsKey(), new Object[]{}, errMsg, LocaleContextHolder.getLocale());
        return new ResponseEntity<GenericResponseDto>(GenericResponseDto.failure(nlsMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<GenericResponseDto> handleException(BindException bindException) {
        logger.error("bean validation error", bindException);
        BindingResult result = bindException.getBindingResult();
        String errorMsg = messageSource.getMessage(result.getFieldError(), LocaleContextHolder.getLocale());
        return new ResponseEntity<GenericResponseDto>(GenericResponseDto.failure(errorMsg), HttpStatus.OK);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<GenericResponseDto> handleException(HttpRequestMethodNotSupportedException exception) {
        String errMsg = exception.getMessage() == null ? "general error" : exception.getMessage();
        logger.error(errMsg, exception);
        StringBuilder msg = new StringBuilder("method: ").append(exception.getMethod()).append("\n");
        for (HttpMethod httpMethod : exception.getSupportedHttpMethods()) {
            msg.append(httpMethod.name()).append(", ");
        }
        emailController.sendInternalErrorMail(msg.toString(), exception);
        return new ResponseEntity<GenericResponseDto>(GenericResponseDto.failure(errMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<GenericResponseDto> handleException(Exception exception) {
        String errMsg = exception.getMessage() == null ? "general error" : exception.getMessage();
        logger.error(errMsg, exception);
        emailController.sendInternalErrorMail("handleException", exception);
        return new ResponseEntity<GenericResponseDto>(GenericResponseDto.failure(errMsg), HttpStatus.BAD_REQUEST);
    }
}
