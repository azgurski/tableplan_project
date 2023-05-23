package com.zgurski.controller.exceptionhandle;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.IllegalRequestException;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.util.RandomValuesGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.zgurski.controller.response.ApplicationErrorCodes.BAD_REQUEST_CREATE_ENTITY;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler {

    private static final Logger log = Logger.getLogger(DefaultExceptionHandler.class);

    private final RandomValuesGenerator generator;

    @ExceptionHandler({
            EmptyResultDataAccessException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Object> handleInvalidInputValueException(Exception ex) {

        ErrorContainer error = buildErrorContainer(ex, 40001, "Invalid input value(s).");
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            NumberFormatException.class,
            InvalidFormatException.class,
            IllegalStateException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleNumberFormatException(Exception ex) {

        ErrorContainer error = buildErrorContainer(ex, 40002, "Invalid input value(s).");
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ConversionFailedException.class
    })
    public ResponseEntity<Object> handleConversionException(ConversionFailedException ex) {

        ErrorContainer error = buildErrorContainer(ex, 40001, ex.getCause().getLocalizedMessage().toString());
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Object> handleMethodArgumentException(MethodArgumentNotValidException ex) {

            StringBuilder fieldsErrorMessage = new StringBuilder();
            List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

            for (FieldError fieldError : fieldErrors) {

                fieldsErrorMessage.append(fieldError.getField())
                        .append(" - ").append(fieldError.getDefaultMessage())
                        .append("; ");
            }

        ErrorContainer error = buildErrorContainer(ex, 40001, fieldsErrorMessage.toString());
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<Object> handleIllegalRequestException(IllegalRequestException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        String collect = bindingResult.getAllErrors().stream().
                map(ObjectError::toString)
                .collect(Collectors.joining(", "));

        ErrorContainer error = buildErrorContainer(ex, 40002, collect);

        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            EntityNotFoundException.class
    })
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {

        ErrorContainer error = buildErrorContainer(ex, 40401, ex.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FailedTransactionException.class)
    public ResponseEntity<Object> handleTransactionalException(FailedTransactionException ex) {

        ErrorContainer error = buildErrorContainer(ex, 20401, "Failed to create or update.");
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.NO_CONTENT);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {

        ErrorContainer error = buildErrorContainer(ex, 50001, "Internal server error.");
        return new ResponseEntity<>(Collections.singletonMap("error", error), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorContainer buildErrorContainer(Exception ex, int errorCode, String message) {

        String errorUniqueId = generator.uuidGenerator();
        log.error(errorUniqueId + ex.getMessage(), ex);

        ErrorContainer error = ErrorContainer.builder()
                .errorId(errorUniqueId)
                .errorCode(errorCode)
                .errorMessage(message)
                .errorClass(ex.getClass().getSimpleName().toString())
                .build();

        return error;
    }


//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException e) {
//        /* Handles all other exceptions. Status code 500. */
//
//        String exceptionUniqueId = generator.uuidGenerator();
//
//        log.error(exceptionUniqueId + e.getMessage(), e);
//
//        return new ResponseEntity<>(
//                new ErrorMessage(
//                        exceptionUniqueId,
//                        FATAL_ERROR.getCodeId(),
//                        e.getMessage() + " by Runtime exception handler."
//                ),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}


//    @ExceptionHandler(IllegalRequestException.class)
//    public ResponseEntity<ErrorMessage> handleIllegalRequestException(IllegalRequestException e) {
//
//        String exceptionUniqueId = generator.uuidGenerator();
//
//        BindingResult bindingResult = e.getBindingResult();
//        String collect = bindingResult.getAllErrors().stream().
//                map(ObjectError::toString)
//                .collect(Collectors.joining(", "));
//
//        log.error(exceptionUniqueId + e.getMessage(), e);
//
//        return new ResponseEntity<>(
//                new ErrorMessage(
//                        exceptionUniqueId,
//                        BAD_REQUEST_CREATE_ENTITY.getCodeId(),
//                        collect
////                        e.getMessage() + " by checked exception handler."
//                ),
//                HttpStatus.BAD_REQUEST);
//    }
