package com.ecommerce.dto.order;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
    @NotBlank
    private String shippingAddress;
    @NotBlank
    private String phone;

    public CheckoutRequest() {}
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
