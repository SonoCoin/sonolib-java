package io.sonocoin.sonolib.dtos.extended;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class NonceDto {
    @NotNull
    public BigInteger confirmedNonce;
    @NotNull
    public BigInteger unconfirmedNonce;
}
