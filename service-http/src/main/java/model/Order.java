package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {
    private String id;
    private BigDecimal amount;
    private String email;

    public Order(String id, BigDecimal amount, String email) {
        this.id = id;
        this.amount = amount;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
