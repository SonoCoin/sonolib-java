package io.sonocoin.sonolib.client.responses;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class ConsumedFeeDto {
    @NotNull
    public BigInteger consumedFee;
    @NotNull
    public String result;
}
