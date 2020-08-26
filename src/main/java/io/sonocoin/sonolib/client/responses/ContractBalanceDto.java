package io.sonocoin.sonolib.client.responses;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ContractBalanceDto {

    @NotNull
    public ContractDto contract;
    @NotNull
    public BigDecimal balance;

}
