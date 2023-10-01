package com.online.store.controller;

import com.online.store.model.Item;
import com.online.store.model.Order;
import com.online.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/store")
@Validated
@RequiredArgsConstructor
@Slf4j
public class StoreController {
  private final StoreService storeService;
  @GetMapping("/items")
  @Operation(summary = "Get Items In store with pagination support")
  public ResponseEntity<List<Item>> getItems(
          @RequestParam(defaultValue = "1") @Min(1) int pageNumber, @RequestParam(defaultValue = "3") int pageSize) {
    log.info("Get Items request received with pageNumber: {} and pageSize: {}",pageNumber,pageSize);
    return ResponseEntity.ok(storeService.fetchItems(pageNumber, pageSize));
  }

  @PostMapping("/order/checkout")
  @Operation(summary = "order checkout")
  public ResponseEntity<Long> orderCheckout( @Valid @RequestBody Order order) {
    log.info("order checkout request received with order: {}",order);
    Long orderId = storeService.orderCheckout(order);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
  }

  @GetMapping("/order/{orderId}")
  @Operation(summary = "Get Order with orderId")
  public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
    log.info("Get Order request received for orderId: {}",orderId);
    Optional<Order> order = storeService.getOrder(orderId);
    return order
        .map(ResponseEntity::ok)
        .orElseGet(
            () -> {
              log.error(
                  "get order request failed as there is no order with given orderId: {}", orderId);
              return ResponseEntity.notFound().build();
            });
  }
}
