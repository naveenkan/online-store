package com.online.store.service;

import com.online.store.exception.ItemNotFoundException;
import com.online.store.model.Item;
import com.online.store.model.Order;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Service
public class StoreService {
  private final Map<Long, Order> orderMap = new LinkedHashMap<>();
  private final Map<Long, Item> itemMap = new LinkedHashMap<>();
  private final AtomicLong orderIdGenerator = new AtomicLong(1);

  public StoreService() {
    LongStream.rangeClosed(1, 10)
        .mapToObj(
            i ->
                Item.builder()
                    .itemId(i)
                    .imageUrl("https://img.com/img" + i)
                    .name("item" + i)
                    .description("item description" + i)
                    .price(1000.00 * i)
                    .discountPercentage(2 * i)
                    .build())
        .forEach(item -> itemMap.put(item.getItemId(), item));
  }

  public List<Item> fetchItems(int pageNumber, int pageSize) {
    List<Item> items = new ArrayList<>(itemMap.values());
    int start = (pageNumber - 1) * pageSize;
    int end = Math.min(start + pageSize, items.size()); //
    if (start >= items.size()) {
      return Collections.emptyList();
    }
    return items.subList(start, end);
  }

  public Long orderCheckout(Order order) {
    if (!itemMap.containsKey(order.getItemId())) {
      throw new ItemNotFoundException(order.getItemId());
    }
    Long orderId = orderIdGenerator.getAndIncrement();
    order.setOrderId(orderId);
    orderMap.put(orderId, order);
    return orderId;
  }

  public Optional<Order> getOrder(Long orderId) {
    return Optional.ofNullable(orderMap.get(orderId));
  }

}
