package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {
    private String userId;
    private String orderId;
    private BigDecimal amount;
    private String email;

    public Order(String id, String orderId, BigDecimal amount) {
        this.userId = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId=" + userId +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
