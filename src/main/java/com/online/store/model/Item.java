package com.online.store.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
  private Long itemId;
  private String imageUrl;
  private String name;
  private String description;
  private Double price;

  private float discountPercentage;
}
