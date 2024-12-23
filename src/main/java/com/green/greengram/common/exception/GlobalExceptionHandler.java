package com.green.greengram.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice //AOP(Aspect Orientation Programming, 관점 지향 프로그래밍)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //(추가메소드) 우리가 커스텀한 예외가 발생되었을 경우 캐치
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleException(CustomException e) {
        return handleExeptionInternal(e.getErrorCode());
    }

    @ExceptionHandler({ MalformedJwtException.class, SignatureException.class }) //토큰값이 유효하지 않을 때, 토큰이 오염되었을 때
    public ResponseEntity<Object> handleMalformedJwtException() {
        return handleExeptionInternal(UserErrorCode.INVALID_TOKEN);
    }

    @ExceptionHandler(ExpiredJwtException.class) //토큰이 만료가 되었을 때
    public ResponseEntity<Object> handleExceptionInternal() {
        return handleExeptionInternal(UserErrorCode.EXPIRED_TOKEN);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
                                                                        , HttpHeaders headers
                                                                        , HttpStatusCode statusCode
                                                                        , WebRequest request) {
        return handleExeptionInternal(CommonErrorCode.INVALID_PARAMETER, ex);
    }

    private ResponseEntity<Object> handleExeptionInternal(ErrorCode errorCode) {
        return handleExeptionInternal(errorCode, null);
    }

    //오버로딩
    private ResponseEntity<Object> handleExeptionInternal(ErrorCode errorCode, BindException e) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, e));
    }

    private MyErrorResponse makeErrorResponse(ErrorCode errorCode, BindException e) {
        return MyErrorResponse.builder() //이미 MyErrorResponse에서 String으로 지정했기 때문에
                .resultMsg(errorCode.getMessage())
                .resultData(errorCode.name())
                .valids(e == null ? null : getValidationErrors(e))
                .build();
    }

    private List<MyErrorResponse.ValidationError> getValidationErrors(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        //List<FieldError> fieldErrors = e.getFieldErrors();

        List<MyErrorResponse.ValidationError> errors = new ArrayList<>(fieldErrors.size());
        for(FieldError fieldError : fieldErrors) {
            errors.add(MyErrorResponse.ValidationError.of(fieldError));
        }
        return errors;
    }
}
