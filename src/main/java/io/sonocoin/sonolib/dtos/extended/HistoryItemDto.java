package io.sonocoin.sonolib.dtos.extended;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Date;

public class HistoryItemDto {

    @NotNull
    public String network;
    @Nullable
    public String blockHash;
    @Nullable
    public String txHash;
    @NotNull
    public String ticker;
    @NotNull
    public boolean isToken;
    @NotNull
    public BigDecimal commission;
    @NotNull
    public BigDecimal amount;
    @NotNull
    public String operationType;
    @NotNull
    public String type;
    @NotNull
    public String fromAddress;
    @NotNull
    public String toAddress;
    @Nullable
    public String contractAddress;
    @Nullable
    public Date dt;
    @NotNull
    public String status;
    @Nullable
    public String nodeId;
    @NotNull
    public BigDecimal priceUsd;

}
