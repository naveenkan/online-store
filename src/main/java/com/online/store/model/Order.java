package com.online.store.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
  private long OrderId;
  private long ItemId;

  @NotBlank(message = "FullName is required")
  @Pattern(
      regexp = "^[A-Za-z ]+$",
      message = "Name should only contain letters A-Z, a-z, and spaces")
  private String FullName;

  private String Address;

  @Email(message = "should have a valid email address syntax")
  private String email;

  @Pattern(
      regexp = "^\\d{3}-\\d{3}-\\d{4}$",
      message = "Phone number should be in the format xxx-xxx-xxxx")
  private String phoneNumber;

  @Pattern(
      regexp = "^\\d{19}$",
      message = "Credit card should be 19 digits long and contain only digits")
  private String creditCardNumber;
}
