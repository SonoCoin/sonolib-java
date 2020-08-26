package io.sonocoin.sonolib.client.requests;

public class StaticCallRequestDto {
    public String address;
    public String payload;

    public StaticCallRequestDto() {

    }

    public StaticCallRequestDto(String address, String payload) {
        this.address = address;
        this.payload = payload;
    }
}
