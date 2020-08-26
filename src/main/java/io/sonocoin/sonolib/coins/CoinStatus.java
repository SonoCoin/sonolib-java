package io.sonocoin.sonolib.coins;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CoinStatus {
    Active(1),
    Spent(2),;

    private final int value;

    private CoinStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}