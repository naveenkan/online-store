package com.online.store.exception;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException(Long itemId) {
    super("Item Not found with id: " + itemId);
  }
}
