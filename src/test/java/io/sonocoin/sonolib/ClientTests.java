package io.sonocoin.sonolib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sonocoin.sonolib.client.Client;
import org.junit.Test;
import io.sonocoin.sonolib.client.ErrorDto;
import io.sonocoin.sonolib.dtos.extended.BalanceDto;
import io.sonocoin.sonolib.dtos.extended.NonceDto;

public class ClientTests {

    private final ObjectMapper mapper;
    private final static String baseAddr = "https://testnet.sonocoin.io/api/rest/v1";
    private final Client client;

    public ClientTests() {
        this.client = new Client(this.baseAddr);
        this.mapper = new ObjectMapper();
    }

    private void debug(Object x) throws JsonProcessingException {
        String json = this.mapper.writeValueAsString(x);
        System.out.println(json);
    }

    @Test
    public void errorTest() throws JsonProcessingException {
        String err = "{\"status\":\"Invalid request\",\"code\":400,\"message\":\"invalid structure\"}";
        ErrorDto res = this.mapper.readValue(err, ErrorDto.class);
        debug(res);
    }

    @Test
    public void balanceTest() {
        String address = "SCfCPbtKBkj4CNP3D2ywNRTJV2jWU6ubdEz";

        try {
            BalanceDto balance = client.getBalance(address);
            debug(balance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void nonceTest() {
        String address = "SCfCPbtKBkj4CNP3D2ywNRTJV2jWU6ubdEz";

        try {
            NonceDto resp = client.getNonce(address);
            debug(resp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
