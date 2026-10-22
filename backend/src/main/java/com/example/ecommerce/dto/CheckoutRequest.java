package com.example.ecommerce.dto;

import com.example.ecommerce.entity.PaymentMethod;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class CheckoutRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Recipient name is required")
    @Size(min = 2, max = 100, message = "Recipient name must be between 2 and 100 characters")
    private String recipientName;

    @NotBlank(message = "Contact phone is required")
    @Size(min = 5, max = 20, message = "Contact phone must be between 5 and 20 characters")
    private String contactPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(min = 5, max = 1000, message = "Shipping address must be between 5 and 1000 characters")
    private String shippingAddress;

    private String billingAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    private String notes;

    public CheckoutRequest() {
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
