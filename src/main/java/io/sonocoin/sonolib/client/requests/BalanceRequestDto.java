package io.sonocoin.sonolib.client.requests;

import java.util.List;

public class BalanceRequestDto {

    public List<String> addresses;

    public BalanceRequestDto(List<String> addresses) {
        this.addresses = addresses;
    }

}
