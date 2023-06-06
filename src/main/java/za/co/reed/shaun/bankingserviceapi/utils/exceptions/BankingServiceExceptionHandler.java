package za.co.reed.shaun.bankingserviceapi.utils.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import za.co.reed.shaun.bankingserviceapi.model.response.error.ErrorHandlingResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.error.FieldValidationResponse;
import za.co.reed.shaun.bankingserviceapi.model.response.error.FieldValidationErrorDetails;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class BankingServiceExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<FieldValidationResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        ArrayList<FieldValidationErrorDetails> fieldValidationErrorDetails = new ArrayList<>();
        for (FieldError fieldError : errors) {
            FieldValidationErrorDetails error = new FieldValidationErrorDetails();
            error.setFieldName(fieldError.getField());
            error.setMessage(fieldError.getDefaultMessage());
            fieldValidationErrorDetails.add(error);
        }

        FieldValidationResponse fieldValidationResponse = new FieldValidationResponse();
        fieldValidationResponse.setErrors(fieldValidationErrorDetails);


        return ResponseEntity.badRequest().body(fieldValidationResponse);
    }

    @ExceptionHandler(BankingServiceException.AccountExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ErrorHandlingResponse> handleWithdrawalFromAccountException(BankingServiceException.AccountExistsException ex,
                                                                                      HttpServletRequest request) {

        ErrorHandlingResponse errorHandlingResponse = new ErrorHandlingResponse();
        errorHandlingResponse.setMessage(ex.getMessage());
        errorHandlingResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorHandlingResponse);
    }

    @ExceptionHandler(BankingServiceException.AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErrorHandlingResponse> handleAccountNotFoundException(BankingServiceException.AccountNotFoundException ex,
                                                                 HttpServletRequest request) {

        ErrorHandlingResponse errorHandlingResponse = new ErrorHandlingResponse();
        errorHandlingResponse.setMessage(ex.getMessage());
        errorHandlingResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorHandlingResponse);
    }

    @ExceptionHandler(BankingServiceException.AccountTransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorHandlingResponse> handleAccountTransactionException(BankingServiceException.AccountTransactionException ex,
                                                                                   HttpServletRequest request) {
        ErrorHandlingResponse errorHandlingResponse = new ErrorHandlingResponse();
        errorHandlingResponse.setMessage(ex.getMessage());
        errorHandlingResponse.setPath(request.getRequestURI());

        return ResponseEntity.badRequest().body(errorHandlingResponse);
    }

    @ExceptionHandler(BankingServiceException.AccountTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErrorHandlingResponse> handleAccountTypeNotFoundException(BankingServiceException.AccountTypeNotFoundException ex,
                                                                                   HttpServletRequest request) {
        ErrorHandlingResponse errorHandlingResponse = new ErrorHandlingResponse();
        errorHandlingResponse.setMessage(ex.getMessage());
        errorHandlingResponse.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorHandlingResponse);
    }
}
