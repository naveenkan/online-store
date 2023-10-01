package com.online.store.exceptionhandler;

import com.online.store.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class StoreExceptionHandler {

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<String> ItemNotFoundExceptionHandler(ItemNotFoundException exception) {
    log.warn(exception.getMessage());
    return ResponseEntity.badRequest().body(exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  Map<String, String> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    Map<String, String> map = new LinkedHashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      map.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    log.warn("bean validation exception with violations: {}", map);
    return map;
  }
}
