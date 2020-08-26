package io.sonocoin.sonolib.dtos;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TxObjectType {
    Input(0),
    Transfer(1),
    Stake(2),
    Message(3),
    TransferCommission(4),
    StakeCommission(5),
    MessageCommission(6);

    private final int value;

    private TxObjectType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
