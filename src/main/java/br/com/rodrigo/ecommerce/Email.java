package br.com.rodrigo.ecommerce;

import java.math.BigDecimal;

public class Email {

    private final String userId;
    private final String oderId;
    private final BigDecimal amount;

    public Email(String userId, String oderId, BigDecimal amount) {
        this.userId = userId;
        this.oderId = oderId;
        this.amount = amount;
    }
}
