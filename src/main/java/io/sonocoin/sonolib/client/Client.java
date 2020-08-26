package io.sonocoin.sonolib.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sonocoin.sonolib.client.requests.StaticCallRequestDto;
import io.sonocoin.sonolib.client.responses.ConsumedFeeDto;
import io.sonocoin.sonolib.client.responses.StaticCallDto;
import io.sonocoin.sonolib.dtos.ContractMessageDto;
import io.sonocoin.sonolib.dtos.TransactionRequest;
import io.sonocoin.sonolib.dtos.extended.BalanceDto;
import io.sonocoin.sonolib.dtos.extended.NonceDto;
import io.sonocoin.sonolib.dtos.extended.TxPublishResponseDto;
import io.sonocoin.sonolib.misc.Sono;
import okhttp3.*;

import java.math.BigInteger;
import java.util.Objects;

public class Client {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String baseAddr;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public Client(String baseAddr) {
        this.client = new OkHttpClient();
        this.baseAddr = baseAddr;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public BalanceDto getBalance(String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/account/" + address + "/balance")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), BalanceDto.class);
    }

    public NonceDto getNonce(String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/account/" + address + "/nonce")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), NonceDto.class);
    }

    public NonceDto getAllowanceNonce(String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/wallet/" + address + "/allowance_nonce")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), NonceDto.class);
    }

    public boolean send(TransactionRequest tx) throws Exception {
        String json = this.mapper.writeValueAsString(tx);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/txs/publish")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }
        TxPublishResponseDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), TxPublishResponseDto.class);

        return res.result.equals("ok");
    }

    public BigInteger consumedFee(String sender, String contract, String payload) throws Exception {
        return consumedFee(sender, contract, payload, Sono.zero, Sono.zero);
    }

    public BigInteger consumedFee(String sender, String contract, String payload, BigInteger value, BigInteger commission) throws Exception {
        ContractMessageDto msg = new ContractMessageDto(sender, contract, payload, value, commission);
        String json = this.mapper.writeValueAsString(msg);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contract/consumed_fee")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }
        ConsumedFeeDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ConsumedFeeDto.class);
        return res.consumedFee;
    }

    public String staticCall(String contract, String payload) throws Exception {
        StaticCallRequestDto req = new StaticCallRequestDto(contract, payload);
        String json = this.mapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contract/static_call")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }
        StaticCallDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), StaticCallDto.class);
        return res.result;
    }

}
