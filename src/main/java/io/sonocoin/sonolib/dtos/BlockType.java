package io.sonocoin.sonolib.dtos;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BlockType {
    Epoch(0),
    Block(1),
    Failover(2),;

    private final int value;

    private BlockType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}