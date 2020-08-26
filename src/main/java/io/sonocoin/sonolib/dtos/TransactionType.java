package io.sonocoin.sonolib.dtos;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    Account(0),
    Coinbase(1),
    Genesis(2);

    private final int value;

    private TransactionType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
