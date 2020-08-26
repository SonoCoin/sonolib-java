package io.sonocoin.sonolib.dtos.extended;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class BalanceDto {

    @Nullable
    public String address;
    @NotNull
    public BigInteger confirmedAmount;
    @NotNull
    public BigInteger unconfirmedAmount;

}
