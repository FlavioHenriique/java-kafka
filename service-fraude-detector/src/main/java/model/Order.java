package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private String email;

    public Order(String id, String orderId, BigDecimal amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", email='" + email + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }
}
