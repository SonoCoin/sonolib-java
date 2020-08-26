package io.sonocoin.sonolib.client.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractDto {

    @NotNull
    public String address;
    @Nullable
    public Date createdDt;
    @NotNull
    public String sender;
    @NotNull
    public String type;
    @Nullable
    public String name;
    @Nullable
    public String symbol;
    @Nullable
    public int decimals;
    @Nullable
    public BigInteger totalSupply;
    @Nullable
    public String website;
    @NotNull
    public boolean isParsed;
    @NotNull
    public String txHash;
    @NotNull
    public String network;

}
