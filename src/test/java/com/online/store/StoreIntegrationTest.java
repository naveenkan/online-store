package com.online.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.store.model.Item;
import com.online.store.model.Order;
import com.online.store.service.StoreService;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreIntegrationTest {
  private static final String BASE_API_URL = "/api/v1/store";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private StoreService storeService;

  @Test
  public void shouldGetItems() throws Exception {
    RequestBuilder request =
        MockMvcRequestBuilders.get(BASE_API_URL + "/items")
            .queryParam("pageNumber", "1")
            .queryParam("pageSize", "3")
            .contentType(MediaType.APPLICATION_JSON);
    String responseAsString =
        mockMvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    List<Item> itemList =
        objectMapper.readValue(responseAsString, new TypeReference<List<Item>>() {});
    Assertions.assertThat(itemList).hasSize(3);
  }

  @Test
  public void shouldDoOrderCheckout() throws Exception {
    // Given
    Order order =
        Order.builder()
            .ItemId(1)
            .FullName("Naveen Kandagatla")
            .Address("Bangalore,India")
            .creditCardNumber("1234567891234567891")
            .email("test@gmail.com")
            .phoneNumber("123-456-7890")
            .build();

    RequestBuilder request =
        MockMvcRequestBuilders.post(BASE_API_URL + "/order/checkout")
            .content(objectMapper.writeValueAsString(order))
            .contentType(MediaType.APPLICATION_JSON);

    String response =
        mockMvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Assertions.assertThat(response).isEqualTo("1");
  }

  @Test
  public void shouldGetOrder() throws Exception {
    // Given
    Order order =
        Order.builder()
            .ItemId(1)
            .FullName("Naveen Kandagatla")
            .Address("Bangalore,India")
            .creditCardNumber("1234567891234567891")
            .email("test@gmail.com")
            .phoneNumber("123-456-7890")
            .build();

    storeService.orderCheckout(order);

    RequestBuilder request =
        MockMvcRequestBuilders.get(BASE_API_URL + "/order" + "/1")
            .accept(MediaType.APPLICATION_JSON);
    String responseAsString =
        mockMvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var expectOrder = objectMapper.readValue(responseAsString, Order.class);

    SoftAssertions.assertSoftly(
        it -> {
          it.assertThat(expectOrder.getItemId()).isEqualTo(1);
          it.assertThat(expectOrder.getAddress()).isEqualTo(expectOrder.getAddress());
          it.assertThat(expectOrder.getCreditCardNumber())
              .isEqualTo(expectOrder.getCreditCardNumber());
        });
  }

  @Test
  @DisplayName("should not submit order when item is not there")
  public void shouldFailOrderCheckoutWhenNoItem() throws Exception {
    // Given
    Order order =
        Order.builder()
            .ItemId(100)
            .FullName("Naveen Kandagatla")
            .Address("Bangalore,India")
            .creditCardNumber("1234567891234567891")
            .email("test@gmail.com")
            .phoneNumber("123-456-7890")
            .build();

    RequestBuilder request =
        MockMvcRequestBuilders.post(BASE_API_URL + "/order/checkout")
            .content(objectMapper.writeValueAsString(order))
            .contentType(MediaType.APPLICATION_JSON);

    String response =
        mockMvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Assertions.assertThat(response).isEqualTo("Item Not found with id: " + order.getItemId());
  }

  @Test
  @DisplayName("should not submit order when validation failed")
  public void shouldFailOrderCheckoutWithValidation() throws Exception {
    // Given
    Order order =
        Order.builder()
            .ItemId(1)
            .FullName("Naveen123")
            .Address("Bangalore,India")
            .creditCardNumber("1234")
            .email("testEmail")
            .phoneNumber("123-7890")
            .build();

    RequestBuilder request =
        MockMvcRequestBuilders.post(BASE_API_URL + "/order/checkout")
            .content(objectMapper.writeValueAsString(order))
            .contentType(MediaType.APPLICATION_JSON);

    String response =
        mockMvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String expectedResponseError =
        "{\"FullName\":\"Name should only contain letters A-Z, a-z, and spaces\",\"email\":\"should have a valid email address syntax\",\"phoneNumber\":\"Phone number should be in the format xxx-xxx-xxxx\",\"creditCardNumber\":\"Credit card should be 19 digits long and contain only digits\"}";
    Assertions.assertThat(objectMapper.readTree(response))
        .isEqualTo(objectMapper.readTree(expectedResponseError));
  }

  @Test
  @DisplayName("should return not 404 when no order")
  public void shouldNotAbleToGetOrder() throws Exception {

    // Given
    long orderId = 100;

    // When && Then
    RequestBuilder request =
        MockMvcRequestBuilders.get(BASE_API_URL + "/order/" + orderId)
            .accept(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
