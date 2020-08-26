package io.sonocoin.sonolib.dtos.extended;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class BalanceExtendedDto {

    @NotNull
    public String address;
    @NotNull
    public BigDecimal priceUsd;
    @NotNull
    public BigDecimal confirmedAmount;
    @NotNull
    public BigDecimal unconfirmedAmount;

}
