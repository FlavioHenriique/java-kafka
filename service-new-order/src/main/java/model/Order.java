package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {
    private int id;
    private String orderId;
    private BigDecimal amount;
    private String email;

    public Order(int id, String orderId, BigDecimal amount, String email) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
